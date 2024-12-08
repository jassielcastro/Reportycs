package repository.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RepositoryRemoteResponseData(
    val data: RepositoryData
)

@Serializable
data class RepositoryData(
    val repository: PullRequestByRepository
)

@Serializable
data class PullRequestByRepository(
    val pullRequests: PullRequestsRemoteData
)

@Serializable
data class PullRequestsRemoteData(
    val nodes: List<PullRequestRemoteInfo>
)

@Serializable
data class PullRequestRemoteInfo(
    val number: Int,
    val title: String,
    val author: PRAuthorInfo,
    val mergedAt: String?,
)

@Serializable
data class PRAuthorInfo(
    val login: String,
    val avatarUrl: String
)
