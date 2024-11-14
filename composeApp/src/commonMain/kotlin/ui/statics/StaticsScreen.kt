package ui.statics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.ic_git_merge
import jirareports.composeapp.generated.resources.ic_team
import jirareports.composeapp.generated.resources.loading_info_message_1
import jirareports.composeapp.generated.resources.loading_info_message_2
import jirareports.composeapp.generated.resources.loading_info_message_3
import jirareports.composeapp.generated.resources.loading_info_message_4
import jirareports.composeapp.generated.resources.loading_info_message_5
import org.koin.compose.rememberKoinInject
import ui.components.CountersItem
import ui.components.FailureScreen
import ui.components.IdleScreen
import ui.components.LoadingScreen
import ui.components.charts.BarChart
import ui.components.charts.LineChart
import ui.components.charts.PieChart
import ui.model.GithubStats
import ui.model.UiState

@Composable
fun StaticsScreen(
    modifier: Modifier = Modifier,
    repositoryName: String
) {

    val viewModel = rememberKoinInject<StaticsViewModel>()
    val pullRequestInfoState by viewModel.pullRequestInfoState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPullRequestInformation(repositoryName)
    }

    AnimatedContent(
        targetState = pullRequestInfoState,
        transitionSpec = {
            fadeIn() + slideInVertically(animationSpec = spring(
                dampingRatio = 0.8f,
                stiffness = Spring.StiffnessLow
            ), initialOffsetY = { fullHeight -> fullHeight }) togetherWith
                    fadeOut(animationSpec = tween(200))
        },
        modifier = modifier
    ) { state ->
        when (state) {
            UiState.Failure -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = "Ooooh no... algo raro ha pasdo..."
                )
            }

            UiState.Unauthorized -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = "Ooooh no... Creo que no estás autorizado para realizar esta acción"
                )
            }

            UiState.NoInternet -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = "Ooooh no... Creo que no tienes interneto!"
                )
            }

            UiState.Idle -> {
                IdleScreen(modifier = Modifier.fillMaxSize())
            }

            UiState.Loading -> {
                LoadingScreen(
                    modifier = Modifier.fillMaxSize(),
                    loadingText = listOf(
                        Res.string.loading_info_message_1,
                        Res.string.loading_info_message_2,
                        Res.string.loading_info_message_3,
                        Res.string.loading_info_message_4,
                        Res.string.loading_info_message_5
                    )
                )
            }

            is UiState.Success -> {
                StaticsItemsScreen(
                    githubStats = state.data
                )
            }
        }
    }
}

@Composable
fun StaticsItemsScreen(
    modifier: Modifier = Modifier,
    githubStats: GithubStats
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.17f)
                        .fillMaxHeight()
                ) {
                    CountersItem(
                        title = "Analyzed PRs",
                        count = {
                            "${githubStats.prsCount}"
                        },
                        icon = Res.drawable.ic_git_merge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    )

                    CountersItem(
                        title = "Code owners",
                        count = {
                            "${githubStats.activeDevelopers}"
                        },
                        icon = Res.drawable.ic_team,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BarChart(
                        modifier = Modifier
                            .fillMaxSize(),
                        title = "PullRequest merged",
                        barChartData = githubStats.pullRequestByOwnerBarData,
                        labels = githubStats.ownerNames
                    )
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(640.dp)
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.45f)
                        .fillMaxHeight()
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    ) {
                        BarChart(
                            modifier = Modifier
                                .fillMaxSize(),
                            title = "Owner participation (reviews)",
                            barChartData = githubStats.pullRequestReviewsByOwnerBarData,
                            labels = githubStats.ownerNames
                        )
                    }

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        LineChart(
                            modifier = Modifier
                                .fillMaxSize(),
                            title = "Change request in PR",
                            dataPoints = githubStats.pullRequestCommentsLineData,
                        )
                    }
                }

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxSize()
                ) {
                    PieChart(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        title = "Comments received in PRs",
                        pieChartData = githubStats.givenCommentsPieChartData,
                    )
                }
            }
        }
    }
}
