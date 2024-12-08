package repository.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RepositoryStaticsRequest(
    val query: String,
    val variables: RepositoryStaticsParams
)

@Serializable
data class RepositoryStaticsParams(
    val repoName: String,
    val ownerName: String,
    val branch: String
)
