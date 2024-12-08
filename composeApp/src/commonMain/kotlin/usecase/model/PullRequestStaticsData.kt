package usecase.model

data class PullRequestStaticsData(
    val number: Int,
    val title: String,
    val author: String,
    val createdAt: String,
    val mergedAt: String,
    val comments: List<CommentsBy>,
    val approves: List<ApprovedBy>,
)

data class CommentsBy(
    val author: String,
)

data class ApprovedBy(
    val author: String,
)
