package ui.repositories

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.PullRequestRepository
import ui.model.GithubStats
import ui.model.UiState

class RepositoryViewModel(
    private val repository: PullRequestRepository
) : ViewModel() {

    private val _generalState: MutableStateFlow<UiState<GithubStats>> =
        MutableStateFlow(UiState.Idle)
    val generalState = _generalState.asStateFlow()

    /*val activeDevelopers = listOf(
        "alanme_backbase",
        "rafael-calderon-cwbva_backbase", // @Rafael Calderón
        "javierv_backbase", // @javierv
        "daniel-gongora-cwbva_backbase", // @Daniel Gongora
        "sael_backbase", // @Sael
        "juan-fonseca-cwbva_backbase", // @Juan Antonio Fonseca Razo
        "jesus_backbase", // @jesus
        "d-martinez-cwbva_backbase", // @David Martinez
        "edwina_backbase", // @edwina
        "diego-davila-cwbva_backbase", // @Diego Dávila
        "nestor-ruiz-cwbva_backbase", // @Nestor Ruiz
        "ernesto-ortiz-cwbva_backbase", // @Ernesto Ortiz Peralta
        "angeld_backbase", // @angeld
        "cristian-navarro-cwbva_backbase", // @Cristian Navarro González
        "salvador-sanchezt-cwbva_backbase", // @Salvador Amado Sánchez Tochihuitl
        "edgarb_backbase", // @edgarb
        "edgars_backbase", // @edgars
    )

    suspend fun generatePullRequestStats(
        repositoryRequest: RepositoryRequest,
        statsRequest: StatsRequest = StatsRequest()
    ) {
        _generalState.value = UiState.Loading

        runCatching {
            repository.getPullRequestStats(
                repositoryRequest, statsRequest
            )
        }.onSuccess { stats ->
            _generalState.value = UiState.Success(
                GithubStats(
                    prsCount = stats.size,
                    prsByContributor = calculatePrsByContributor(stats),
                    commentsByPr = calculateCommentsByPr(stats),
                    approvalsAndCommentsByContributor = calculateApprovalsAndCommentsByContributor(
                        stats
                    ),
                    statsByType = calculateStatsByType(stats),
                )
            )
        }.onFailure {
            it.printStackTrace()
            _generalState.value = UiState.Failure
        }
    }

    private fun calculatePrsByContributor(prStats: List<PullRequestStat>): Map<String, Int> {
        return prStats.groupBy { it.author }.mapValues { (_, prs) -> prs.size }
    }

    private fun calculateApprovalsByContributor(prStats: List<PullRequestStat>): Map<String, Int> {
        val approvalsCount = mutableMapOf<String, Int>()
        for (pr in prStats) {
            for (approve in pr.approves) {
                approvalsCount[approve.user] = approvalsCount.getOrDefault(approve.user, 0) + 1
            }
        }
        return approvalsCount.filterKeys { activeDevelopers.contains(it) }
    }

    private fun calculateCommentsByPr(prStats: List<PullRequestStat>): Int {
        return prStats.sumOf { it.reviewCommentsCount }
    }

    private fun calculateCommentsByContributor(prStats: List<PullRequestStat>): Map<String, Int> {
        return prStats.groupBy { it.author }
            .mapValues { (_, prs) -> prs.sumOf { it.reviewCommentsCount } }
    }

    private fun calculateApprovalsAndCommentsByContributor(prStats: List<PullRequestStat>): Map<String, Pair<Int, Int>> {
        val approvalsByContributor = calculateApprovalsByContributor(prStats)
        val commentsByContributor = calculateCommentsByContributor(prStats)
        return approvalsByContributor.mapValues { (contributor, prCount) ->
            Pair(prCount, commentsByContributor.getOrDefault(contributor, 0))
        }
    }

    private fun calculateStatsByType(prStats: List<PullRequestStat>): Map<String, Int> {
        val typeStatsMap = mutableMapOf<String, Int>()
        prStats.forEach { pr ->
            val type = extractPrType(pr.title)
            val count = typeStatsMap.getOrPut(type) { 0 }
            typeStatsMap[type] = count + 1
        }

        return typeStatsMap
    }

    private fun extractPrType(title: String): String {
        val regex = Regex("""\[(\w+)]""")
        return regex.find(title)?.groups?.get(1)?.value ?: "UNKNOWN"
    }*/
}
