package ui.statics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.PullRequestRepository
import repository.model.CodeOwnerData
import repository.model.RepositoryData
import repository.model.StaticData
import ui.model.GithubStats
import ui.model.OwnerStats
import ui.model.PullRequestByOwner
import ui.model.PullRequestComments
import ui.model.PullRequestReviewedByOwner
import ui.model.PullRequestType
import ui.model.UiState

class StaticsViewModel(
    private val repository: PullRequestRepository
) : ViewModel() {

    private val _pullRequestInfoState: MutableStateFlow<UiState<GithubStats>> =
        MutableStateFlow(UiState.Idle)
    val pullRequestInfoState = _pullRequestInfoState.asStateFlow()

    suspend fun fetchPullRequestInformation(repositoryName: String) {
        _pullRequestInfoState.value = UiState.Loading
        try {
            val repositoryData = repository.selectRepositoryBy(repositoryName)
                ?: return run {
                    _pullRequestInfoState.value = UiState.Failure
                }
            repository.fetchPullRequestApproves(repositoryData)
            delay(200L)
            repository.fetchPullRequestComments(repositoryData)

            generateStatics(repositoryData)
        } catch (ignore: Exception) {
            _pullRequestInfoState.value = UiState.Failure
        }
    }

    private fun generateStatics(repositoryData: RepositoryData) {
        val pullRequestList = repository.getPullRequestInformation(repositoryData.id)
        val activeOwners = repository.getCodeOwners(repositoryData)

        val stats = GithubStats(
            prsCount = pullRequestList.size,
            pullRequestByOwner = calculatePrsCreatedByContributor(pullRequestList, activeOwners),
            pullRequestReviewedByOwner = calculateApprovalsDistribution(
                pullRequestList,
                activeOwners
            ),
            pullRequestComments = calculateCommentsByPr(pullRequestList),
            ownerStats = calculatePrsAndCommentsCorrelation(
                pullRequestList,
                activeOwners
            ),
            statsByType = calculateStatsByType(pullRequestList),
            activeDevelopers = activeOwners.size,
        )

        println("StaticsViewModel.generateStatics ---> $stats")
        _pullRequestInfoState.value = UiState.Success(stats)
    }

    /**
     * Show the number of PRs created by each author
     */
    private fun calculatePrsCreatedByContributor(
        staticData: List<StaticData>,
        codeOwners: List<CodeOwnerData>
    ): List<PullRequestByOwner> {
        val prsByContributor = staticData.groupingBy { it.author }
            .eachCount()
            .toMutableMap()

        codeOwners.forEach { codeOwner ->
            prsByContributor.putIfAbsent(codeOwner.name, 0)
        }

        return prsByContributor.map { pr ->
            PullRequestByOwner(
                author = pr.key,
                pullRequestCreated = pr.value
            )
        }
    }

    /**
     * Show the distribution of approvals among the different code owners
     */
    private fun calculateApprovalsDistribution(
        staticData: List<StaticData>,
        codeOwners: List<CodeOwnerData>
    ): List<PullRequestReviewedByOwner> {
        val codeOwnerNames = codeOwners.map { it.name }.toSet()
        val approvalCounts = mutableMapOf<String, Int>()

        staticData.forEach { pr ->
            pr.approves.forEach { approver ->
                if (approver in codeOwnerNames) {
                    approvalCounts[approver] = approvalCounts.getOrDefault(approver, 0) + 1
                }
            }
        }

        codeOwners.forEach { codeOwner ->
            approvalCounts.putIfAbsent(codeOwner.name, 0)
        }

        return approvalCounts.map {
            PullRequestReviewedByOwner(
                user = it.key,
                pullRequestReviewed = it.value,
            )
        }
    }

    /**
     * Show the number of review comments for each PR
     */
    private fun calculateCommentsByPr(staticData: List<StaticData>): List<PullRequestComments> {
        return staticData
            .associate { it.id to it.reviewCommentsCount }
            .map {
                PullRequestComments(
                    pullRequestId = it.key,
                    reviewCommentsCount = it.value,
                )
            }
    }

    /**
     * Show the correlation between the number of PRs created by a contributor and the number of review comments on those PRs.
     */
    private fun calculatePrsAndCommentsCorrelation(
        staticData: List<StaticData>,
        codeOwners: List<CodeOwnerData>
    ): List<OwnerStats> {
        val prsByContributor = mutableMapOf<String, Int>()
        val commentsByPR = mutableMapOf<String, Int>()

        staticData.forEach { pr ->
            prsByContributor[pr.author] = prsByContributor.getOrDefault(pr.author, 0) + 1
            commentsByPR[pr.author] =
                commentsByPR.getOrDefault(pr.author, 0) + pr.reviewCommentsCount
        }

        codeOwners.forEach { codeOwner ->
            prsByContributor.putIfAbsent(codeOwner.name, 0)
            commentsByPR.putIfAbsent(codeOwner.name, 0)
        }

        return prsByContributor.keys.associateWith {
            Pair(prsByContributor[it] ?: 0, commentsByPR[it] ?: 0)
        }.map {
            OwnerStats(
                user = it.key,
                pullRequestCreated = it.value.first,
                commentsByPr = it.value.second
            )
        }
    }

    private fun calculateStatsByType(prStats: List<StaticData>): List<PullRequestType> {
        val typeStatsMap = mutableMapOf<String, Int>()
        prStats.forEach { pr ->
            val type = extractPrType(pr.title)
            val count = typeStatsMap.getOrPut(type) { 0 }
            typeStatsMap[type] = count + 1
        }

        return typeStatsMap.map {
            PullRequestType(
                type = it.key,
                count = it.value
            )
        }
    }

    private fun extractPrType(title: String): String {
        val regex = Regex("""\[(\w+)]""")
        return regex.find(title)?.groups?.get(1)?.value ?: "UNKNOWN"
    }
}
