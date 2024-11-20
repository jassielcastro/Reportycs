package ui.statics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import usecase.PullRequestUseCase
import usecase.model.CodeOwnerData
import usecase.model.RepositoryData
import usecase.model.StaticData
import ui.components.charts.BarChartData
import ui.components.charts.PieChartData
import ui.model.GithubStats
import ui.model.UiState
import ui.theme.chartBarsColor2
import ui.theme.chartBarsColor3
import ui.theme.pieChartColors

class StaticsViewModel(
    private val repository: PullRequestUseCase
) : ViewModel() {

    private val _pullRequestInfoState: MutableStateFlow<UiState<GithubStats>> =
        MutableStateFlow(UiState.Idle)
    val pullRequestInfoState = _pullRequestInfoState.asStateFlow()

    suspend fun fetchPullRequestInformation(
        repositoryName: String,
        delay: Long = 200L
    ) {
        _pullRequestInfoState.value = UiState.Loading
        try {
            val repositoryData = repository.selectRepositoryBy(repositoryName)
                ?: return run {
                    _pullRequestInfoState.value = UiState.Failure
                }
            repository.fetchPullRequestApproves(repositoryData)
            delay(delay)
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
            // Bars
            pullRequestByOwner = calculatePrsCreatedByContributor(
                pullRequestList,
                activeOwners
            ),
            pullRequestReviewedByOwner = calculateApprovalsDistribution(
                pullRequestList,
                activeOwners
            ), // Bars
            pullRequestComments = calculateCommentsByPr(pullRequestList), // Line
            ownerStats = calculateCommentsByPR(
                pullRequestList,
                activeOwners
            ), // Double bars
            statsByType = calculateStatsByType(pullRequestList), // Bubble
            activeDevelopers = activeOwners.size,
            ownerNames = generateOwnerNames(activeOwners)
        )

        _pullRequestInfoState.value = UiState.Success(stats)
    }

    private fun generateOwnerNames(codeOwners: List<CodeOwnerData>): List<String> {
        return codeOwners
            .sortedBy { it.name }
            .map {
                it.name
            }
    }

    /**
     * Show the number of PRs created by each author
     */
    private fun calculatePrsCreatedByContributor(
        staticData: List<StaticData>,
        codeOwners: List<CodeOwnerData>,
    ): BarChartData {
        val prsByContributor = staticData.groupingBy { it.author }
            .eachCount()
            .toMutableMap()

        codeOwners.forEach { codeOwner ->
            prsByContributor.putIfAbsent(codeOwner.name, 0)
        }

        val bars = prsByContributor
            .toList()
            .sortedBy { it.first }
            .map { pr ->
                BarChartData.Bar(
                    key = pr.first,
                    value = pr.second.toFloat(),
                )
            }

        return BarChartData(
            bars = bars,
            roundToIntegers = true,
        )
    }

    /**
     * Show the distribution of approvals among the different code owners
     */
    private fun calculateApprovalsDistribution(
        staticData: List<StaticData>,
        codeOwners: List<CodeOwnerData>
    ): BarChartData {
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

        val bars = approvalCounts.map { pr ->
            BarChartData.Bar(
                key = pr.key,
                value = pr.value.toFloat(),
                color = chartBarsColor2
            )
        }

        return BarChartData(
            bars = bars,
            maxBarValue = bars.maxOf { it.value },
            roundToIntegers = true,
        )
    }

    /**
     * Show the number of review comments for each PR
     */
    private fun calculateCommentsByPr(staticData: List<StaticData>): List<Int> {
        return staticData
            .associate { it.id to it.reviewCommentsCount }
            .map { it.value }
    }

    /**
     * Show the correlation between the number of PRs created by a contributor and the number of review comments on those PRs.
     */
    private fun calculateCommentsByPR(
        staticData: List<StaticData>,
        codeOwners: List<CodeOwnerData>
    ): PieChartData {
        val commentsByPR = mutableMapOf<String, Int>()
        val slices = mutableListOf<PieChartData.Slice>()

        staticData.forEach { pr ->
            commentsByPR[pr.author] =
                commentsByPR.getOrDefault(pr.author, 0) + pr.reviewCommentsCount
        }

        codeOwners.forEach { codeOwner ->
            commentsByPR.putIfAbsent(codeOwner.name, 0)
        }

        commentsByPR.toList()
            .sortedBy { it.second }
            .onEachIndexed { index, entry ->
                slices.add(
                    PieChartData.Slice(
                        title = entry.first,
                        value = entry.second,
                        color = pieChartColors[index % pieChartColors.size]
                    )
                )
            }

        val sortedSlices = slices.sortedByDescending { it.value }
        val uniqueTopNumbers = sortedSlices.distinctBy { it.value }.map { it.value }.take(5)
        val topFiveSlices = sortedSlices
            .filter { it.value in uniqueTopNumbers }

        return PieChartData(
            slices = topFiveSlices
        )
    }

    private fun calculateStatsByType(prStats: List<StaticData>): BarChartData {
        val typeStatsMap = mutableMapOf<String, Int>()
        availableTypes().forEach { type ->
            typeStatsMap[type] = 0
        }

        prStats.forEach { pr ->
            val type = extractPrType(pr.title)
            val count = typeStatsMap.getOrPut(type) { 0 }
            typeStatsMap[type] = count + 1
        }

        val bars = typeStatsMap.map {
            BarChartData.Bar(
                key = it.key,
                value = it.value.toFloat(),
                color = chartBarsColor3
            )
        }.sortedByDescending {
            it.value
        }

        return BarChartData(
            bars = bars,
            maxBarValue = bars.maxOf { it.value },
            roundToIntegers = true,
        )
    }

    private fun availableTypes(): List<String> {
        return listOf(
            "FEAT",
            "BUG",
            "DOCS",
            "STYLE",
            "REFACTOR",
            "PERF",
            "TEST",
            "CHORE",
            "UNKNOWN",
        )
    }

    private fun extractPrType(title: String): String {
        val regex = Regex("""\[(\w+)]""")
        return regex.find(title)?.groups?.get(1)?.value ?: "UNKNOWN"
    }
}
