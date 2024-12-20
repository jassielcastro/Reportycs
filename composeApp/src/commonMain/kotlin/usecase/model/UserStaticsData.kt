package usecase.model

data class UserStaticsData(
    val totalContributions: Int,
    val contributionsByWeek: List<ContributionWeek>,
    val commitContributionsByRepository: List<ContributionCommitsByRepository>,
    val issueContributions: List<IssueData>,
    val pullRequestContributions: List<PullRequestContributionData>,
    val pullRequestReviewContributions: List<PullRequestContributionData>,
    val contributionChartData: ContributionChart
)

data class ContributionWeek(
    val weeks: List<Contribution>
)

data class Contribution(
    val contributions: Int
)

data class ContributionCommitsByRepository(
    val repository: RepositoryContributionData,
    val contributions: List<CommitData>
)

data class RepositoryContributionData(
    val name: String,
    val owner: String,
    val url: String
)

data class CommitData(
    val commitCount: Int
)

data class IssueData(
    val title: String,
    val createdAt: String
)

data class PullRequestContributionData(
    val title: String,
    val createdAt: String,
    val number: Int,
    val author: String?,
)

data class ContributionChart(
    val commitsCount: Int,
    val pullRequestCount: Int,
    val reviewsCount: Int,
    val issueCount: Int,
)
