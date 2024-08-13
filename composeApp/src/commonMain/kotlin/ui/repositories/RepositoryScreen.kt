package ui.repositories

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.rememberKoinInject
import repository.model.RepositoryData
import ui.components.CountersScreen
import ui.components.FailureScreen
import ui.components.IdleScreen
import ui.components.LoadingScreen
import ui.model.GithubStats
import ui.model.UiState

@Composable
fun RepositoryScreen(
    modifier: Modifier = Modifier,
    repository: RepositoryData
) {
    val viewModel = rememberKoinInject<RepositoryViewModel>()
    val generalState by viewModel.generalState.collectAsState()

    /*LaunchedEffect(Unit) {
        viewModel.generatePullRequestStats(
            repositoryRequest = RepositoryRequest(
                id = repository.id,
                owner = repository.owner,
                repo = repository.repository,
                token = repository.token,
            )
        )
    }*/

    AnimatedContent(
        targetState = generalState,
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
                LoadingScreen(modifier = Modifier.fillMaxSize())
            }

            is UiState.Success -> {
                RepositoryStats(
                    modifier = Modifier.fillMaxSize(),
                    state.data,
                    0//viewModel.activeDevelopers.size
                )
            }
        }
    }
}

@Composable
fun RepositoryStats(
    modifier: Modifier = Modifier,
    githubStats: GithubStats,
    activeDevelopers: Int
) {

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .fillMaxHeight()
            ) {
                CountersScreen(
                    title = "Analyzed PRs",
                    count = {
                        "${githubStats.prsCount}"
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .fillMaxHeight()
            ) {
                CountersScreen(
                    title = "Bugs average",
                    count = {
                        val bugs = (githubStats.statsByType["BUG"] ?: 0).toFloat()
                        val bugsAverage = (bugs / githubStats.prsCount) * 100
                        "${bugsAverage.toInt()}%"
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
            ) {
                CountersScreen(
                    title = "Comments by PR",
                    count = {
                        val comments = (githubStats.commentsByPr)
                        val commentsAverage = (comments / githubStats.prsCount)
                        "$commentsAverage"
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
            ) {
                CountersScreen(
                    title = "Reviewers average",
                    count = {
                        val reviewers =
                            (githubStats.approvalsAndCommentsByContributor.keys.size).toFloat()
                        val reviewersAverage = (reviewers / activeDevelopers) * 100
                        "${reviewersAverage.toInt()}%"
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {

        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {

        }
    }
}
