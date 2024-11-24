package repository.remote

import crypt.CryptoHandler
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import repository.ext.handleResponse
import repository.model.ErrorStatus
import repository.model.ResponseStatus
import repository.remote.model.request.RepositoryRequest
import repository.remote.model.request.StatsRequest
import repository.remote.model.response.ApproveResponse
import repository.remote.model.response.PullRequestResponse
import repository.remote.model.response.PullRequestInfoResponse

class RemotePullRequestRepository(
    private val client: HttpClient,
    private val cryptoHandler: CryptoHandler
) {
    suspend fun getPullRequest(
        repository: RepositoryRequest,
        stats: StatsRequest
    ): ResponseStatus<List<PullRequestResponse>> {
        val token = cryptoHandler.decrypt(repository.token)
        val response = client.get("/repos/${repository.owner}/${repository.repo}/pulls") {
            bearerAuth(token)
            parameter("page", stats.page)
            parameter("per_page", stats.perPage)
            parameter("base", stats.baseBranch)
            parameter("state", stats.state)
            parameter("sort", stats.sort)
            parameter("direction", stats.direction)
        }

        val result = response.handleResponse<List<PullRequestResponse>>()

        if (result is ResponseStatus.Success) {
            return if (result.response.isEmpty()) {
                ResponseStatus.Error(ErrorStatus.EMPTY)
            } else {
                ResponseStatus.Success(
                    result.response.filter { it.mergedAt != null }
                )
            }
        }

        return result
    }

    suspend fun getPullRequestInfo(
        request: RepositoryRequest,
        pullRequestId: Int,
    ): ResponseStatus<PullRequestInfoResponse> {
        val token = cryptoHandler.decrypt(request.token)
        return client.get("/repos/${request.owner}/${request.repo}/pulls/${pullRequestId}") {
            bearerAuth(token)
        }.handleResponse()
    }

    suspend fun getPullRequestApproves(
        request: RepositoryRequest,
        pullRequestId: Int,
    ): ResponseStatus<List<ApproveResponse>> {
        val token = cryptoHandler.decrypt(request.token)
        val result =
            client.get("/repos/${request.owner}/${request.repo}/pulls/${pullRequestId}/reviews") {
                bearerAuth(token)
            }.handleResponse<List<ApproveResponse>>()

        if (result is ResponseStatus.Success) {
            val filteredData = result.response
                .filter { it.state == "APPROVED" }

            return if (filteredData.isEmpty()) {
                ResponseStatus.Error(ErrorStatus.EMPTY)
            } else {
                ResponseStatus.Success(filteredData)
            }
        }

        return result
    }
}
