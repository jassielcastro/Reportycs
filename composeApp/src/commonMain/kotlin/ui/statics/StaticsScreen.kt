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
import androidx.compose.foundation.layout.Spacer
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
import jirareports.composeapp.generated.resources.analytics_pana
import jirareports.composeapp.generated.resources.analytics_rafiki
import jirareports.composeapp.generated.resources.ic_git_merge
import jirareports.composeapp.generated.resources.ic_team
import jirareports.composeapp.generated.resources.loading_info_message_1
import jirareports.composeapp.generated.resources.loading_info_message_2
import jirareports.composeapp.generated.resources.loading_info_message_3
import jirareports.composeapp.generated.resources.loading_info_message_4
import jirareports.composeapp.generated.resources.loading_info_message_5
import jirareports.composeapp.generated.resources.team_goals_rafiki
import jirareports.composeapp.generated.resources.version_control_bro
import jirareports.composeapp.generated.resources.version_control_rafiki
import org.koin.compose.rememberKoinInject
import ui.components.CountersItem
import ui.components.FailureScreen
import ui.components.IdleScreen
import ui.components.LoadingItem
import ui.components.LoadingScreen
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
                FailureScreen(modifier = Modifier.fillMaxSize())
            }

            UiState.Idle -> {
                IdleScreen(modifier = Modifier.fillMaxSize())
            }

            UiState.Loading -> {
                LoadingScreen(
                    modifier = Modifier.fillMaxSize(),
                    items = listOf(
                        LoadingItem(Res.drawable.analytics_pana, Res.string.loading_info_message_1),
                        LoadingItem(
                            Res.drawable.analytics_rafiki,
                            Res.string.loading_info_message_2
                        ),
                        LoadingItem(
                            Res.drawable.team_goals_rafiki,
                            Res.string.loading_info_message_3
                        ),
                        LoadingItem(
                            Res.drawable.version_control_bro,
                            Res.string.loading_info_message_4
                        ),
                        LoadingItem(
                            Res.drawable.version_control_rafiki,
                            Res.string.loading_info_message_5
                        )
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
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                }
            }
        }

        item {
            Surface (
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

            }
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

            }
        }
    }
}
