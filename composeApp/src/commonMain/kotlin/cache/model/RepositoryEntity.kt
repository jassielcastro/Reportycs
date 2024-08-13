package cache.model

data class RepositoryEntity(
    val id: Int = 0,
    val owner: String,
    val repository: String,
    val token: String
)
