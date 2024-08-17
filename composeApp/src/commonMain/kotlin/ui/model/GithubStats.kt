package ui.model

data class GithubStats(
    val prsCount: Int,
    val pullRequestByOwner: List<PullRequestByOwner>,
    val pullRequestReviewedByOwner: List<PullRequestReviewedByOwner>,
    val pullRequestComments: List<PullRequestComments>,
    val ownerStats: List<OwnerStats>,
    val statsByType: List<PullRequestType>,
    val activeDevelopers: Int
) {

}

data class PullRequestByOwner(
    val author: String,
    val pullRequestCreated: Int
)

data class PullRequestReviewedByOwner(
    val user: String,
    val pullRequestReviewed: Int
)

data class PullRequestComments(
    val pullRequestId: Int,
    val reviewCommentsCount: Int
)

data class OwnerStats(
    val user: String,
    val pullRequestCreated: Int,
    val commentsByPr: Int
)

data class PullRequestType(
    val type: String,
    val count: Int
)
