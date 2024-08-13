package usecase.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequestInfoResponse(
    val merged: Boolean,
    @SerialName("review_comments")
    val reviewComments: Int
)
