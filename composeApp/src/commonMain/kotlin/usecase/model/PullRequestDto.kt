package usecase.model

data class PullRequestDto(
    val id: Int,
    val repositoryId: Int,
    val title: String,
    val author: String,
    val avatar: String,
)
