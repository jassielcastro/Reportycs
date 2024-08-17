package usecase.model

data class StaticDto(
    val id: Int,
    val repositoryId: Int,
    val title: String,
    val author: String,
    val avatar: String,
    val reviewCommentsCount: Int,
    val approves: List<String>,
)
