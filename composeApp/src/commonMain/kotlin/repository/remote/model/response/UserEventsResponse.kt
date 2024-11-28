package repository.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class GitHubContributionsResponse(
    val data: UserData
)

@Serializable
data class UserData(
    val user: UserContributions
)

@Serializable
data class UserContributions(
    val contributionsCollection: ContributionsCollection
)

@Serializable
data class ContributionsCollection(
    val contributionCalendar: ContributionCalendar,
    val commitContributionsByRepository: List<CommitContributionsByRepository>,
    val issueContributions: Contributions<IssueNode>,
    val pullRequestContributions: Contributions<PullRequestNode>,
    val pullRequestReviewContributions: Contributions<PullRequestNode>
)

@Serializable
data class ContributionCalendar(
    val totalContributions: Int,
    val weeks: List<Week>
)

@Serializable
data class Week(
    val contributionDays: List<ContributionDay>
)

@Serializable
data class ContributionDay(
    val contributionCount: Int
)

@Serializable
data class CommitContributionsByRepository(
    val repository: Repository,
    val contributions: Contributions<CommitNode>
)

@Serializable
data class Repository(
    val name: String,
    val owner: Author,
    val url: String
)

@Serializable
data class Author(
    val login: String?,
)

@Serializable
data class Contributions<T>(
    val totalCount: Int,
    val edges: List<Edge<T>>
)

@Serializable
data class Edge<T>(
    val node: T
)

@Serializable
data class CommitNode(
    val commitCount: Int
)

@Serializable
data class IssueNode(
    val issue: Issue
)

@Serializable
data class Issue(
    val title: String,
    val createdAt: String
)

@Serializable
data class PullRequestNode(
    val pullRequest: PullRequest
)

@Serializable
data class PullRequest(
    val title: String,
    val createdAt: String,
    val number: Int,
    val author: Author?
)
