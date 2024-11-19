package ui.model

import androidx.compose.runtime.Immutable
import ui.components.charts.BarChartData
import ui.components.charts.PieChartData

@Immutable
data class GithubStats(
    val prsCount: Int,
    val pullRequestByOwner: BarChartData,
    val pullRequestReviewedByOwner: BarChartData,
    val pullRequestComments: List<Int>,
    val ownerStats: PieChartData,
    val statsByType: BarChartData,
    val activeDevelopers: Int,
    val ownerNames: List<String>
)
