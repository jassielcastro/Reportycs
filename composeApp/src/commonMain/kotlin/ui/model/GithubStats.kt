package ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.components.charts.BarChartData
import ui.components.charts.PieChartData
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

    private val colors: List<Color> by lazy {
        listOf(
            Color(0xff45496a),
            Color(0xff7d8bae),
            Color(0xffe5857b),
            Color(0xfff1b2b2),
            Color(0xffe8ccc7),
            Color(0xff8e65ab),
            Color(0xffdc94b0),
            Color(0xffdc94b0),
            Color(0xffed884c),
            Color(0xffbd2630),
            Color(0xff991b27),
            Color(0xff9ac5e5),
            Color(0xff4fb19d),
            Color(0xffc98c9a),
            Color(0xff065758),
            Color(0xffa9d4d6),
            Color(0xffa9d4d6),
            Color(0xff82c3c5),
        )
    }

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

    val givenCommentsPieChartData: PieChartData by lazy {
        PieChartData(
            slices = ownerStats.sortedByDescending { it.commentsByPr }.mapIndexed { index, ownerStats ->
                PieChartData.Slice(
                    title = ownerStats.user,
                    value = ownerStats.commentsByPr,
                    color = colors[index % colors.size]
                )
            }
        )
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
