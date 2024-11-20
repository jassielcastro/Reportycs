package repository.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApproveResponse(
    val user: Approver,
    val state: String
)

@Serializable
data class Approver(
    @SerialName("login")
    val name: String
)
