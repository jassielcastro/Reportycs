package repository.remote

import crypt.CryptoHandler
import ext.aYearAgo
import ext.formatAsGithub
import ext.now
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import repository.ext.handleResponse
import repository.model.ResponseStatus
import repository.remote.model.request.UserContributionsRequest
import repository.remote.model.request.UserGraphQl
import repository.remote.model.response.GitHubContributionsResponse

class RemoteUserRepository(
    private val client: HttpClient,
    private val cryptoHandler: CryptoHandler
) {

    suspend fun loadUserRepositories(
        token: String,
        userName: String
    ) {
        val graphQLQuery = USER_CONTRIBUTION_GRAPH.trimIndent()

        val now = now().formatAsGithub()
        val aYearAgo = aYearAgo().formatAsGithub()

        val payload = UserContributionsRequest(
            query = graphQLQuery,
            variables = UserGraphQl(
                username = userName,
                from = aYearAgo,
                to =  now
            )
        )

        val response = client.post("https://api.github.com/graphql") {
            accept(ContentType.parse("application/json; charset=utf-8"))
            contentType(ContentType.parse("application/json; charset=utf-8"))
            bearerAuth(token)
            setBody<UserContributionsRequest>(payload)
        }.handleResponse<GitHubContributionsResponse>()

        if (response is ResponseStatus.Success) {
            println("RemoteUserUseCase.loadUserRepositories ---> ${response.response.data.user.contributionsCollection.contributionCalendar}")
            println("RemoteUserUseCase.loadUserRepositories ---> ${response.response.data.user.contributionsCollection.commitContributionsByRepository}")
            println("RemoteUserUseCase.loadUserRepositories ---> ${response.response.data.user.contributionsCollection.issueContributions}")
            println("RemoteUserUseCase.loadUserRepositories ---> ${response.response.data.user.contributionsCollection.pullRequestContributions}")
            println("RemoteUserUseCase.loadUserRepositories ---> ${response.response.data.user.contributionsCollection.pullRequestReviewContributions}")
        }
    }

    private companion object {
        const val USER_CONTRIBUTION_GRAPH = """
            query(${'$'}username: String!, ${'$'}from: DateTime!, ${'$'}to: DateTime!) {
                user(login: ${'$'}username) {
                    contributionsCollection(from: ${'$'}from, to: ${'$'}to) {
                        contributionCalendar {
                            totalContributions
                            weeks {
                                contributionDays {
                                    contributionCount
                                }
                            }
                        }
                        commitContributionsByRepository {
                            repository {
                                name
                                url
                            }
                            contributions(last: 100) {
                                edges {
                                    node {
                                        commitCount
                                    }
                                }
                            }
                        }
                        issueContributions(last: 100) {
                            edges {
                                node {
                                    issue {
                                        title
                                        createdAt
                                    }
                                }
                            }
                        }
                        pullRequestContributions(last: 100) {
                            edges {
                                node {
                                    pullRequest {
                                        title
                                        createdAt
                                    }
                                }
                            }
                        }
                        pullRequestReviewContributions(last: 100) {
                            edges {
                                node {
                                    pullRequest {
                                        title
                                        createdAt
                                    }
                                }
                            }
                        }
                    }
                }
            }"""
    }
}
