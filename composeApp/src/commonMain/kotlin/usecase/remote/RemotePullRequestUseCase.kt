package usecase.remote

import crypt.CryptoHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import usecase.remote.model.request.RepositoryRequest
import usecase.remote.model.request.StatsRequest
import usecase.remote.model.response.ApproveResponse
import usecase.remote.model.response.CodeOwner
import usecase.remote.model.response.PullRequestResponse
import usecase.remote.model.response.PullRequestInfoResponse

class RemotePullRequestUseCase(
    private val client: HttpClient,
    private val cryptoHandler: CryptoHandler
) {

    suspend fun getCodeOwners(repository: RepositoryRequest): List<CodeOwner> {
        val singlePr = getPullRequest(
            repository = repository,
            stats = StatsRequest(
                perPage = 1
            )
        ).firstOrNull() ?: return emptyList()

        val owners = mutableListOf(
            CodeOwner(singlePr.user.name)
        )

        singlePr.requestedReviews.map { review ->
            owners.add(
                CodeOwner(review.name)
            )
        }

        return owners
    }

    suspend fun getPullRequest(
        repository: RepositoryRequest,
        stats: StatsRequest
    ): List<PullRequestResponse> {
        val token = cryptoHandler.decrypt(repository.token)
        return client.get("/repos/${repository.owner}/${repository.repo}/pulls") {
            bearerAuth(token)
            parameter("page", stats.page)
            parameter("per_page", stats.perPage)
            parameter("base", stats.baseBranch)
            parameter("state", stats.state)
            parameter("sort", stats.sort)
            parameter("direction", stats.direction)
        }.body<List<PullRequestResponse>>()
    }

    suspend fun getPullRequestInfo(
        request: RepositoryRequest,
        pullRequestId: Int,
    ): PullRequestInfoResponse {
        val token = cryptoHandler.decrypt(request.token)
        return client.get("/repos/${request.owner}/${request.repo}/pulls/${pullRequestId}") {
            bearerAuth(token)
        }.body()
    }

    suspend fun getPullRequestApproves(
        request: RepositoryRequest,
        pullRequestId: Int,
    ): List<ApproveResponse> {
        val token = cryptoHandler.decrypt(request.token)
        return client.get("/repos/${request.owner}/${request.repo}/pulls/${pullRequestId}/reviews") {
            bearerAuth(token)
        }.body<List<ApproveResponse>>()
            .filter { it.state == "APPROVED" }
    }
}
