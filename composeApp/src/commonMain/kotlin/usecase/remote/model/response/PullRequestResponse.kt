package usecase.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequestResponse(
    val number: Int,
    val title: String,
    val user: User,
    @SerialName("requested_reviewers")
    val requestedReviews: List<User>,
    @SerialName("merged_at")
    val mergedAt: String?
)

@Serializable
data class User(
    @SerialName("login")
    val name: String,
    @SerialName("avatar_url")
    val avatar: String
)
