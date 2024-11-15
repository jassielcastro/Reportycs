package ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.dashboard_add_new_repositories_button
import jirareports.composeapp.generated.resources.dashboard_repositories_title
import jirareports.composeapp.generated.resources.dashboard_title
import jirareports.composeapp.generated.resources.general_failure_error_message
import jirareports.composeapp.generated.resources.general_no_internet_error_message
import jirareports.composeapp.generated.resources.general_unauthorized_error_message
import jirareports.composeapp.generated.resources.generate_reports_button
import jirareports.composeapp.generated.resources.ic_delete_forever
import jirareports.composeapp.generated.resources.loading_info_message_2
import jirareports.composeapp.generated.resources.restart_token_button
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import repository.model.PullRequestData
import repository.model.RepositoryData
import ui.components.DrawerItem
import ui.components.FailureScreen
import ui.components.IconButton
import ui.components.IdleScreen
import ui.components.LoadingScreen
import ui.components.NormalReportycsButton
import ui.components.PullRequestItem
import ui.components.ReportycsButton
import ui.dashboard.token.RestartTokenScreen
import ui.model.UiState

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    navigateToGenerateReports: (RepositoryData) -> Unit,
    navigateToAddNewRepository: () -> Unit
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
            is UiState.Success -> {
                DashboardRepositoriesScreen(
                    repositories = state.data,
                    viewModel = viewModel,
                    navigateToGenerateReports = navigateToGenerateReports,
                    navigateToAddNewRepository = navigateToAddNewRepository,
                )
            }

            UiState.Failure,
            UiState.NoInternet,
            UiState.Unauthorized -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = Res.string.general_failure_error_message
                ) {
                    viewModel.loadRepositories()
                }
            }

            else -> IdleScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun DashboardRepositoriesScreen(
    modifier: Modifier = Modifier,
    repositories: List<RepositoryData>,
    viewModel: DashboardViewModel,
    navigateToGenerateReports: (RepositoryData) -> Unit,
    navigateToAddNewRepository: () -> Unit
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
                .fillMaxWidth(0.15f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary),
            onSelected = { selected ->
                repositorySelected = selected
            },
            addNewRepository = navigateToAddNewRepository
        )

        PullRequestScreen(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            viewModel = viewModel,
            repositorySelected = repositorySelected,
            onGenerateReportsClicked = { repo ->
                navigateToGenerateReports(repo)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRequestScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel,
    repositorySelected: RepositoryData?,
    onGenerateReportsClicked: (RepositoryData) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showActionsOnSuccess by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            RepositoryHeader(
                showActions = showActionsOnSuccess,
                pullRequestToAnalyze = {
                    repositorySelected?.let { selected ->
                        viewModel.loadPRsToAnalyze(selected.id)
                    } ?: 0
                },
                onGenerateReportsClicked = {
                    repositorySelected?.let { onGenerateReportsClicked(it) }
                },
                onRestartTokenClicked = {
                    showBottomSheet = true
                },
                onDeleteClicked = {
                    repositorySelected?.let { viewModel.deleteRepository(it.id) }
                }
            )

            PullRequestListScreen(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxSize(),
                viewModel = viewModel,
                repositorySelected = repositorySelected,
                onUnauthorizedToken = {
                    showBottomSheet = true
                },
                onStatusChanged = { showButton ->
                    showActionsOnSuccess = showButton
                }
            )
        }

        if (showBottomSheet && repositorySelected != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                containerColor = MaterialTheme.colorScheme.primary,
                sheetState = sheetState
            ) {
                RestartTokenScreen(repositorySelected.id) {
                    scope.launch {
                        viewModel.loadRepositories()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PullRequestListScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel,
    repositorySelected: RepositoryData?,
    onUnauthorizedToken: () -> Unit,
    onStatusChanged: (Boolean) -> Unit
) {
    val pullRequestState by viewModel.pullRequestState.collectAsState()
    val scope = rememberCoroutineScope()

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
        onStatusChanged(state is UiState.Success)
        when (state) {
            UiState.Failure -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = Res.string.general_failure_error_message
                ) {
                    scope.launch {
                        repositorySelected?.let { viewModel.loadPullRequest(it) }
                    }
                }
            }

            UiState.Unauthorized -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = Res.string.general_unauthorized_error_message,
                    retryMessage = Res.string.restart_token_button,
                    retry = onUnauthorizedToken
                )
            }

            UiState.NoInternet -> {
                FailureScreen(
                    modifier = Modifier.fillMaxSize(),
                    message = Res.string.general_no_internet_error_message
                ) {
                    scope.launch {
                        repositorySelected?.let { viewModel.loadPullRequest(it) }
                    }
                }
            }

            UiState.Idle -> {
                IdleScreen(modifier = Modifier.fillMaxSize())
            }

            UiState.Loading -> {
                LoadingScreen(
                    modifier = Modifier.fillMaxSize(),
                    loadingText = listOf(
                        Res.string.loading_info_message_2,
                    )
                )
            }

            is UiState.Success -> {
                PullRequestItemListScreen(
                    pullRequest = state.data
                )
            }
        }
    }
}

@Composable
fun PullRequestItemListScreen(
    modifier: Modifier = Modifier,
    pullRequest: List<PullRequestData>,
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .fillMaxSize()
    ) {
        items(pullRequest) { pr ->
            PullRequestItem(
                pullRequestData = pr
            )
        }
    }
}

@Composable
fun RepositoryHeader(
    modifier: Modifier = Modifier,
    showActions: Boolean,
    pullRequestToAnalyze: () -> Int,
    onGenerateReportsClicked: () -> Unit,
    onRestartTokenClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(top = 4.dp, start = 16.dp)
                .fillMaxWidth(0.45f)
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

        Row(
            modifier = Modifier
                .padding(top = 4.dp, start = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (showActions) {
                NormalReportycsButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentWidth()
                        .height(48.dp),
                    text = stringResource(Res.string.generate_reports_button),
                    onClick = {
                        onGenerateReportsClicked()
                    }
                )
            }

            NormalReportycsButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentWidth()
                    .height(48.dp),
                text = stringResource(Res.string.restart_token_button),
                onClick = {
                    onRestartTokenClicked()
                }
            )

            IconButton(
                icon = Res.drawable.ic_delete_forever,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentWidth()
                    .height(48.dp),
                onClick = {
                    onDeleteClicked()
                }
            )
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
