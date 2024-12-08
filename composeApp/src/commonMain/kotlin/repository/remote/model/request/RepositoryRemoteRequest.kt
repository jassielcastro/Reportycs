package repository.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RepositoryRemoteRequest(
    val query: String,
    val variables: RepositoryQueryParams
)

@Serializable
data class RepositoryQueryParams(
    val repoName: String,
    val ownerName: String,
    val branch: String
)
