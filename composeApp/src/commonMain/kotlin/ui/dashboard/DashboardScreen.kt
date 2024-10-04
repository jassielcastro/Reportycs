package ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.analytics_pana
import jirareports.composeapp.generated.resources.analytics_rafiki
import jirareports.composeapp.generated.resources.dashboard_add_new_repositories_button
import jirareports.composeapp.generated.resources.dashboard_repositories_title
import jirareports.composeapp.generated.resources.dashboard_title
import jirareports.composeapp.generated.resources.generate_reports_button
import jirareports.composeapp.generated.resources.ic_delete_forever
import jirareports.composeapp.generated.resources.loading_info_message_1
import jirareports.composeapp.generated.resources.loading_info_message_2
import jirareports.composeapp.generated.resources.loading_info_message_3
import jirareports.composeapp.generated.resources.loading_info_message_4
import jirareports.composeapp.generated.resources.loading_info_message_5
import jirareports.composeapp.generated.resources.team_goals_rafiki
import jirareports.composeapp.generated.resources.version_control_bro
import jirareports.composeapp.generated.resources.version_control_rafiki
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import repository.model.PullRequestData
import repository.model.RepositoryData
import ui.components.DeleteButton
import ui.components.DrawerItem
import ui.components.FailureScreen
import ui.components.IdleScreen
import ui.components.LoadingItem
import ui.components.LoadingScreen
import ui.components.NormalReportycsButton
import ui.components.PullRequestItem
import ui.components.ReportycsButton
import ui.model.UiState

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onGenerateReports: (RepositoryData) -> Unit,
    addNewRepository: () -> Unit
) {
    val viewModel = rememberKoinInject<DashboardViewModel>()
    val repositoriesState by viewModel.repositoriesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRepositories()
    }

    AnimatedContent(
        targetState = repositoriesState,
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
                        )
                    )
                )
            }

            is UiState.Success -> {
                DashboardRepositoriesScreen(
                    repositories = state.data,
                    viewModel = viewModel,
                    onGenerateReports = onGenerateReports,
                    addNewRepository = addNewRepository
                )
            }
        }
    }
}

@Composable
fun DashboardRepositoriesScreen(
    repositories: List<RepositoryData>,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel,
    onGenerateReports: (RepositoryData) -> Unit,
    addNewRepository: () -> Unit
) {

    var repositorySelected by remember { mutableStateOf(repositories.first()) }

    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        StartDrawerComponent(
            repositories = repositories,
            selectedId = repositorySelected.id,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 24.dp)
                .fillMaxWidth(0.15f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .background(MaterialTheme.colorScheme.secondary),
            onSelected = { selected ->
                repositorySelected = selected
            },
            addNewRepository = addNewRepository
        )

        PullRequestScreen(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            viewModel = viewModel,
            repositorySelected = repositorySelected,
            onGenerateReportsClicked = { repo ->
                onGenerateReports(repo)
            }
        )
    }
}

@Composable
fun RepositoryHeader(
    modifier: Modifier = Modifier,
    pullRequestToAnalyze: () -> Int,
    onGenerateReportsClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(top = 16.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(top = 4.dp, start = 16.dp)
                .fillMaxWidth(0.65f)
        ) {
            Text(
                text = stringResource(Res.string.dashboard_title),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${pullRequestToAnalyze()} PRs to analyze",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )
        }

        NormalReportycsButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(0.8f)
                .height(48.dp),
            text = stringResource(Res.string.generate_reports_button),
            onClick = {
                onGenerateReportsClicked()
            }
        )

        DeleteButton(
            icon = Res.drawable.ic_delete_forever,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                onDeleteClicked()
            }
        )
    }
}

@Composable
fun PullRequestScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel,
    repositorySelected: RepositoryData?,
    onGenerateReportsClicked: (RepositoryData) -> Unit
) {
    val pullRequestState by viewModel.pullRequestState.collectAsState()

    LaunchedEffect(repositorySelected) {
        repositorySelected?.let { viewModel.loadPullRequest(it) }
    }

    AnimatedContent(
        targetState = pullRequestState,
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
                PullRequestListScreen(
                    pullRequest = state.data,
                    pullRequestToAnalyze = {
                        repositorySelected?.let { selected ->
                            viewModel.loadPRsToAnalyze(selected.id)
                        } ?: 0
                    },
                    onGenerateReportsClicked = {
                        if (repositorySelected != null) {
                            onGenerateReportsClicked(repositorySelected)
                        }
                    },
                    onDeleteClicked = {
                        if (repositorySelected != null) {
                            viewModel.deleteRepository(repositorySelected.id)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PullRequestListScreen(
    modifier: Modifier = Modifier,
    pullRequest: List<PullRequestData>,
    pullRequestToAnalyze: () -> Int,
    onGenerateReportsClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .fillMaxSize()
        ) {

            stickyHeader {
                RepositoryHeader(
                    pullRequestToAnalyze = pullRequestToAnalyze,
                    onGenerateReportsClicked = onGenerateReportsClicked,
                    onDeleteClicked = onDeleteClicked
                )
            }

            items(pullRequest) { pr ->
                PullRequestItem(
                    pullRequestData = pr
                )
            }
        }
    }
}

@Composable
fun StartDrawerComponent(
    repositories: List<RepositoryData>,
    selectedId: Int,
    modifier: Modifier = Modifier,
    onSelected: (RepositoryData) -> Unit,
    addNewRepository: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {

            item {
                Text(
                    text = stringResource(Res.string.dashboard_repositories_title),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 12.dp)
                        .fillMaxWidth()
                )
            }

            items(repositories) { repo ->
                DrawerItem(
                    isSelected = repo.id == selectedId,
                    repositoryData = repo
                ) { selected ->
                    onSelected(selected)
                }
            }
        }

        ReportycsButton(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            text = stringResource(Res.string.dashboard_add_new_repositories_button),
            onClick = addNewRepository
        )
    }
}
