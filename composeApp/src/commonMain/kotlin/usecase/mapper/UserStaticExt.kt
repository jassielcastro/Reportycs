package usecase.mapper

import repository.remote.model.response.CommitContributionsByRepository
import repository.remote.model.response.CommitNode
import repository.remote.model.response.ContributionDay
import repository.remote.model.response.Contributions
import repository.remote.model.response.ContributionsCollection
import repository.remote.model.response.GitHubContributionsResponse
import repository.remote.model.response.IssueNode
import repository.remote.model.response.PullRequestNode
import repository.remote.model.response.Repository
import repository.remote.model.response.Week
import usecase.model.CommitData
import usecase.model.Contribution
import usecase.model.ContributionChart
import usecase.model.ContributionCommitsByRepository
import usecase.model.ContributionWeek
import usecase.model.IssueData
import usecase.model.PullRequestContributionData
import usecase.model.RepositoryContributionData
import usecase.model.UserStaticsData

fun GitHubContributionsResponse.toUserStaticData(): UserStaticsData =
    with(this.data.user.contributionsCollection) {
        return UserStaticsData(
            totalContributions = this.contributionCalendar.totalContributions,
            contributionsByWeek = this.contributionCalendar.weeks.toContributionWeekList(),
            commitContributionsByRepository = this.commitContributionsByRepository.toContributionCommitsByRepository(),
            issueContributions = this.issueContributions.toIssueDataList(),
            pullRequestContributions = this.pullRequestContributions.toPullRequestContributionDataList(),
            pullRequestReviewContributions = this.pullRequestReviewContributions.toPullRequestContributionDataList(),
            contributionChartData = this.toContributionChartData()
        )
    }

fun List<Week>.toContributionWeekList(): List<ContributionWeek> {
    return this.map { week ->
        ContributionWeek(
            weeks = week.contributionDays.toContributionDaysList()
        )
    }
}

fun List<ContributionDay>.toContributionDaysList(): List<Contribution> {
    return this.map { day ->
        Contribution(
            contributions = day.contributionCount
        )
    }
}

fun List<CommitContributionsByRepository>.toContributionCommitsByRepository(): List<ContributionCommitsByRepository> {
    return this.map { contribution ->
        ContributionCommitsByRepository(
            repository = contribution.repository.toRepositoryData(),
            contributions = contribution.contributions.toCommitDataList(),
        )
    }
}

fun Repository.toRepositoryData(): RepositoryContributionData {
    return RepositoryContributionData(
        name = this.name,
        owner = this.owner.login.orEmpty(),
        url = this.url,
    )
}

fun Contributions<CommitNode>.toCommitDataList(): List<CommitData> {
    return this.edges.map {
        CommitData(
            commitCount = it.node.commitCount
        )
    }
}

fun Contributions<IssueNode>.toIssueDataList(): List<IssueData> {
    return this.edges.map {
        IssueData(
            title = it.node.issue.title,
            createdAt = it.node.issue.createdAt,
        )
    }
}

fun Contributions<PullRequestNode>.toPullRequestContributionDataList(): List<PullRequestContributionData> {
    return this.edges.map {
        PullRequestContributionData(
            title = it.node.pullRequest.title,
            createdAt = it.node.pullRequest.createdAt,
            number = it.node.pullRequest.number,
            author = it.node.pullRequest.author?.login.orEmpty(),
        )
    }
}

fun ContributionsCollection.toContributionChartData(): ContributionChart {
    return ContributionChart(
        commitsCount = this.commitContributionsByRepository.sumOf { it.contributions.totalCount },
        pullRequestCount = this.pullRequestContributions.totalCount,
        reviewsCount = this.pullRequestReviewContributions.totalCount,
        issueCount = this.issueContributions.totalCount,
    )
}
