package repository.model

data class RepositoryData(
    val id: Int = 0,
    val owner: String,
    val repository: String,
    val token: String
)
