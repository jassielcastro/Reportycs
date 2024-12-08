package repository.remote

import crypt.CryptoHandler
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import repository.ext.handleResponse
import repository.local.LocalTokenRepository
import repository.local.model.RepositoryRequest
import repository.model.ResponseStatus
import repository.remote.model.request.RepositoryQueryParams
import repository.remote.model.request.RepositoryRemoteRequest
import repository.remote.model.request.RepositoryStaticsParams
import repository.remote.model.request.RepositoryStaticsRequest
import repository.remote.model.response.PullRequestInfoResponse
import repository.remote.model.response.RepositoryRemoteResponseData

class RemotePullRequestRepository(
    private val client: HttpClient,
    private val cryptoHandler: CryptoHandler,
    private val localTokenRepository: LocalTokenRepository
) : TokenHandlerDelegate by TokenHandlerDelegate.Impl(localTokenRepository, cryptoHandler) {
    suspend fun getPullRequest(
        repository: RepositoryRequest,
    ): ResponseStatus<RepositoryRemoteResponseData> {
        val payload = RepositoryRemoteRequest(
            query = REPOSITORY_GRAPH,
            variables = RepositoryQueryParams(
                repoName = repository.repo,
                ownerName = repository.owner,
                branch = "develop"
            ),
        )

        return client.post {
            bearerAuth(getDecryptedToken())
            setBody<RepositoryRemoteRequest>(payload)
        }.handleResponse<RepositoryRemoteResponseData>()
    }

    suspend fun getPullRequestInfo(
        request: RepositoryRequest,
    ): ResponseStatus<PullRequestInfoResponse> {

        val payload = RepositoryStaticsRequest(
            query = REPOSITORY_INFO_GRAPH,
            variables = RepositoryStaticsParams(
                repoName = request.repo,
                ownerName = request.owner,
                branch = "develop",
            )
        )

        return client.post {
            bearerAuth(getDecryptedToken())
            setBody<RepositoryStaticsRequest>(payload)
        }.handleResponse<PullRequestInfoResponse>()
    }

    private companion object {
        const val REPOSITORY_GRAPH = """
            query(${'$'}repoName: String!, ${'$'}ownerName: String!, ${'$'}branch: String!) {
              repository(name: ${'$'}repoName, owner: ${'$'}ownerName) {
                pullRequests(baseRefName: ${'$'}branch, last: 100) {
                  nodes {
                    number
                    title
                    author {
                      login
                      avatarUrl
                    }
                    mergedAt
                    createdAt
                  }
                }
              }
            }"""

        const val REPOSITORY_INFO_GRAPH = """
            query(${'$'}repoName: String!, ${'$'}ownerName: String!, ${'$'}branch: String!) {
              repository(name: ${'$'}repoName, owner: ${'$'}ownerName) {
                pullRequests(baseRefName: ${'$'}branch, last: 100) {
                  nodes {
                    number
                    title
                    author {
                      login
                      avatarUrl
                    }
                    createdAt
                    mergedAt
                    reviews(first: 100) {
                      nodes {
                        state
                        author {
                          login
                        }
                      }
                    }
                  }
                }
              }
            }
        """
    }
}
