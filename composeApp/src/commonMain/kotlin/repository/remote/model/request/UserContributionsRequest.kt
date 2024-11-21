package repository.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserContributionsRequest(
    val query: String,
    val variables: UserGraphQl
)

@Serializable
data class UserGraphQl(
    val username: String,
    val from: String,
    val to: String,
)
