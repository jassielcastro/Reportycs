package repository.model

data class OwnerDto(
    val idOwner: Int = 0,
    val user: String,
    val repositoryId: Int
)
