package repository.model

data class RepositoryDto(
    val id: Int = 0,
    val owner: String,
    val repository: String,
)
