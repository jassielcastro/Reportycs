package usecase.remote

import crypt.CryptoHandler
import ext.aYearAgo
import ext.formatAsGithub
import ext.now
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import usecase.remote.model.request.UserContributionsRequest
import usecase.remote.model.request.UserGraphQl

class RemoteUserUseCase(
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
        }

        println("RemoteUserUseCase.loadUserRepositories ---> ${response.body<Any>()}")

        //.handleResponse<List<UserEventsResponse>>()

        /*if (response is ResponseStatus.Success) {
            val repos = response.response.map { it.repo.name }.distinct()
            println("RemoteUserUseCase.loadUserRepositories ---> ${repos.size}")
            println("RemoteUserUseCase.loadUserRepositories ---> $repos")
        }*/
    }

    private companion object {
        const val USER_CONTRIBUTION_GRAPH = """
            query(${'$'}username: String!, ${'$'}from: DateTime!, ${'$'}to: DateTime!) {
                user(login: ${'$'}username) {
                    contributionsCollection(from: ${'$'}from, to: ${'$'}to) {
                        commitContributionsByRepository {
                            repository {
                                name
                                url
                            }
                            contributions(first: 100) {
                                edges {
                                    node {
                                        commitCount
                                    }
                                }
                            }
                        }
                        issueContributions(first: 100) {
                            edges {
                                node {
                                    issue {
                                        title
                                        url
                                        createdAt
                                    }
                                }
                            }
                        }
                        pullRequestContributions(first: 100) {
                            edges {
                                node {
                                    pullRequest {
                                        title
                                        url
                                        createdAt
                                    }
                                }
                            }
                        }
                        pullRequestReviewContributions(first: 100) {
                            edges {
                                node {
                                    pullRequest {
                                        title
                                        url
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
