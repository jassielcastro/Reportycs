package repository.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PullRequestInfoResponse(
    val data: RepositoryStaticsData
)

@Serializable
data class RepositoryStaticsData(
    val repository: RepositoryStaticInfo
)

@Serializable
data class RepositoryStaticInfo(
    val pullRequests: AnalyzedPullRequests
)

@Serializable
data class AnalyzedPullRequests(
    val nodes: List<PullRequestData>
)

@Serializable
data class PullRequestData(
    val title: String,
    val number: Int,
    val author: AuthorPR,
    val createdAt: String,
    val mergedAt: String?,
    val reviews: Reviews
)

@Serializable
data class AuthorPR(
    val login: String,
    val avatarUrl: String
)

@Serializable
data class Reviews(
    val nodes: List<ReviewedBy>
)

@Serializable
data class ReviewedBy(
    val state: String,
    val author: Author,
)
