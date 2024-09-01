package ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.components.charts.BarChartData
import ui.theme.primaryLight

@Immutable
data class GithubStats(
    val prsCount: Int,
    val pullRequestByOwner: List<PullRequestByOwner>,
    val pullRequestReviewedByOwner: List<PullRequestReviewedByOwner>,
    val pullRequestComments: List<PullRequestComments>,
    val ownerStats: List<OwnerStats>,
    val statsByType: List<PullRequestType>,
    val activeDevelopers: Int,
    val ownerNames: List<String>
) {

    val pullRequestByOwnerBarData: BarChartData by lazy {
        val bars = pullRequestByOwner
            .sortedBy { it.author }
            .map { pr ->
            BarChartData.Bar(
                value = pr.pullRequestCreated.toFloat(),
                color = Color(0xfffebe54),
                background = primaryLight
            )
        }
        BarChartData(
            bars = bars,
            roundToIntegers = true,
            barWidth = 60.dp,
        )
    }

    val pullRequestReviewsByOwnerBarData: BarChartData by lazy {
        val bars = pullRequestReviewedByOwner
            .sortedBy { it.user }
            .map { pr ->
                BarChartData.Bar(
                    value = pr.pullRequestReviewed.toFloat(),
                    color = Color(0xff895765),
                    background = primaryLight
                )
            }
        BarChartData(
            bars = bars,
            maxBarValue = prsCount.toFloat(),
            roundToIntegers = true,
            barWidth = 60.dp,
        )
    }

    val pullRequestCommentsLineData: List<Int> by lazy {
        pullRequestComments.map { it.reviewCommentsCount }
    }
}

@Immutable
data class PullRequestByOwner(
    val author: String,
    val pullRequestCreated: Int
)

@Immutable
data class PullRequestReviewedByOwner(
    val user: String,
    val pullRequestReviewed: Int
)

@Immutable
data class PullRequestComments(
    val pullRequestId: Int,
    val reviewCommentsCount: Int
)

@Immutable
data class OwnerStats(
    val user: String,
    val pullRequestCreated: Int,
    val commentsByPr: Int
)

@Immutable
data class PullRequestType(
    val type: String,
    val count: Int
)
