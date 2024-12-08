package repository.local.model

import kotlinx.serialization.Serializable

@Serializable
data class RepositoryRequest(
    val id: Int,
    val owner: String,
    val repo: String,
)
