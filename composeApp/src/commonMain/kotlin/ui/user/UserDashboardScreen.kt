package ui.user

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.general_no_internet_error_message
import jirareports.composeapp.generated.resources.general_unauthorized_error_message
import jirareports.composeapp.generated.resources.users_dashboard_message
import jirareports.composeapp.generated.resources.users_dashboard_search_error_state_message
import jirareports.composeapp.generated.resources.users_dashboard_search_error_state_title
import jirareports.composeapp.generated.resources.users_dashboard_search_hint
import jirareports.composeapp.generated.resources.users_dashboard_search_idle_state_message
import jirareports.composeapp.generated.resources.users_dashboard_search_idle_state_title
import jirareports.composeapp.generated.resources.users_dashboard_search_loading_state_title
import jirareports.composeapp.generated.resources.users_dashboard_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import ui.components.NormalReportycsButton
import ui.components.PullRequestItem
import ui.components.charts.BarChartData
import ui.components.charts.HorizontalBarChart
import ui.components.charts.PieChart
import ui.components.charts.PieChartData
import ui.model.TimePeriod
import ui.model.UiState
import ui.repositories.TextPlaceHolder
import ui.theme.GithubTextOutlinedColor
import ui.theme.InverseGithubButtonOutlinedColor
import ui.theme.InverseSelectedGithubButtonOutlinedColor
import ui.theme.githubContributionColor1
import ui.theme.githubContributionColor2
import ui.theme.githubContributionColor3
import ui.theme.githubContributionColor4
import ui.theme.githubContributionColor5
import ui.theme.githubContributionColorList
import usecase.model.ContributionWeek
import usecase.model.IssueData
import usecase.model.PullRequestContributionData
import usecase.model.UserStaticsData

@Composable
fun UserDashboardScreen(
    modifier: Modifier = Modifier,
) {
    var usernameText by remember { mutableStateOf("") }
    val viewModel = rememberKoinInject<UserDashboardViewModel>()

    val scope = rememberCoroutineScope()
    var selectedPeriod by remember { mutableStateOf(TimePeriod.YEAR) }

    LaunchedEffect(Unit) {
        viewModel.initUserNameListener()
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(Res.string.users_dashboard_title),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(top = 42.dp)
        )

        Text(
            text = stringResource(Res.string.users_dashboard_message),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = usernameText,
            onValueChange = {
                usernameText = it
                viewModel.onUsernameChanged(usernameText)
            },
            placeholder = { TextPlaceHolder(stringResource(Res.string.users_dashboard_search_hint)) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            colors = GithubTextOutlinedColor(),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.4f)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )

        TimePeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = {
                scope.launch {
                    selectedPeriod = it
                    viewModel.onPeriodSelected(selectedPeriod)
                }
            }
        )

        UserStatsScreen(
            modifier = Modifier
                .fillMaxSize(),
            viewModel = viewModel
        )
    }
}

@Composable
fun TimePeriodSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = TimePeriod.entries

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier
                .wrapContentSize(),
        ) {
            periods.forEach { period ->
                val isSelected = period == selectedPeriod
                NormalReportycsButton(
                    text = period.displayName,
                    onClick = {
                        onPeriodSelected(period)
                    },
                    color = if (isSelected) {
                        InverseSelectedGithubButtonOutlinedColor()
                    } else {
                        InverseGithubButtonOutlinedColor()
                    },
                    textColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentWidth()
                        .height(48.dp)
                )

                Spacer(
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        }
    }
}

@Composable
fun UserStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: UserDashboardViewModel
) {
    val userStatsState by viewModel.userStats.collectAsState()

    AnimatedContent(
        targetState = userStatsState,
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
                UserMessageScreen(
                    text = stringResource(Res.string.users_dashboard_search_error_state_title),
                    message = stringResource(Res.string.users_dashboard_search_error_state_message),
                )
            }

            UiState.Idle -> {
                UserMessageScreen(
                    text = stringResource(Res.string.users_dashboard_search_idle_state_title),
                    message = stringResource(Res.string.users_dashboard_search_idle_state_message),
                )
            }

            UiState.Loading -> {
                UserMessageScreen(
                    text = stringResource(Res.string.users_dashboard_search_loading_state_title),
                    message = "",
                )
            }

            UiState.NoInternet -> {
                UserMessageScreen(
                    text = "",
                    message = stringResource(Res.string.general_no_internet_error_message),
                )
            }

            is UiState.Success -> {
                UserStatsList(userData = state.data)
            }

            UiState.Unauthorized -> {
                UserMessageScreen(
                    text = "",
                    message = stringResource(Res.string.general_unauthorized_error_message),
                )
            }
        }
    }
}

@Composable
fun UserStatsList(
    modifier: Modifier = Modifier,
    userData: UserStaticsData
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {

        item {
            ContributionCalendar(
                contributions = userData.contributionsByWeek,
                totalContributions = userData.totalContributions
            )
        }

        item {
            ContributionsCharts(
                userData = userData
            )
        }

        item {
            PullRequestItemListView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                title = "Latest 5 Pull Requests created",
                list = userData.pullRequestContributions
            )
        }

        item {
            PullRequestItemListView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                title = "Latest 5 Pull Requests reviewed",
                list = userData.pullRequestReviewContributions
            )
        }

        item {
            IssuesItemListView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                title = "Latest 5 Issues created",
                list = userData.issueContributions
            )
        }
    }
}

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .padding(24.dp)
            .then(modifier)
    )
}

@Composable
fun ContributionCalendar(
    modifier: Modifier = Modifier,
    contributions: List<ContributionWeek>,
    totalContributions: Int
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Total of contributions: $totalContributions",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Row {
                contributions.forEach { contribution ->
                    Column {
                        contribution.weeks.forEach { contribution ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(2.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                        MaterialTheme.shapes.extraSmall
                                    )
                                    .background(
                                        color = getColorForContributions(contribution.contributions),
                                        shape = MaterialTheme.shapes.extraSmall,
                                    )
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(end = 16.dp)
                )

                githubContributionColorList.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(2.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                MaterialTheme.shapes.extraSmall
                            )
                            .background(
                                color = color,
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                    )
                }

                Text(
                    text = "More",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ContributionsCharts(
    modifier: Modifier = Modifier,
    userData: UserStaticsData
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .height(320.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .fillMaxHeight()
            ) {
                PieChart(
                    modifier = Modifier
                        .size(320.dp),
                    title = "Activity overview",
                    pieChartData = PieChartData(
                        slices = listOf(
                            PieChartData.Slice(
                                "Commits",
                                userData.contributionChartData.commitsCount,
                                githubContributionColor1
                            ),
                            PieChartData.Slice(
                                "Code review",
                                userData.contributionChartData.reviewsCount,
                                githubContributionColor2
                            ),
                            PieChartData.Slice(
                                "Pull Request",
                                userData.contributionChartData.pullRequestCount,
                                githubContributionColor3
                            ),
                            PieChartData.Slice(
                                "Issues",
                                userData.contributionChartData.issueCount,
                                githubContributionColor4
                            )
                        )
                    ),
                )
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                HorizontalBarChart(
                    modifier = modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    title = "Contributions commits by repository",
                    barChartData = BarChartData(
                        roundToIntegers = true,
                        bars = userData.commitContributionsByRepository.map { repo ->
                            BarChartData.Bar(
                                key = "${repo.repository.name}/${repo.repository.owner}",
                                value = repo.contributions.sumOf { it.commitCount }.toFloat(),
                            )
                        },
                        maxBarValue = userData.commitContributionsByRepository.sumOf { contribution ->
                            contribution.contributions.sumOf { it.commitCount }
                        }.plus(20).toFloat()
                    ),
                )
            }
        }
    }
}

@Composable
fun PullRequestItemListView(
    modifier: Modifier = Modifier,
    title: String,
    list: List<PullRequestContributionData>
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Column {
                SectionTitle(title = title)

                list.forEach { pr ->
                    PullRequestItem(
                        modifier = Modifier
                            .padding(24.dp),
                        pullRequestNumber = pr.number,
                        pullRequestAuthor = pr.author ?: "Unknown",
                        pullRequestTitle = pr.title,
                    )
                }
            }
        }
    }
}

@Composable
fun IssuesItemListView(
    modifier: Modifier = Modifier,
    title: String,
    list: List<IssueData>
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Column {
                SectionTitle(title = title)

                list.forEach { issue ->
                    IssueItemView(
                        modifier = Modifier
                            .padding(24.dp),
                        issue = issue
                    )
                }
            }
        }

    }
}

@Composable
fun IssueItemView(
    modifier: Modifier = Modifier,
    issue: IssueData
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = issue.title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
        )

        Text(
            text = issue.createdAt,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(top = 4.dp, start = 16.dp)
        )

        Divider(
            modifier = Modifier
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f)
        )
    }
}

@Composable
fun UserMessageScreen(
    modifier: Modifier = Modifier,
    text: String,
    message: String
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(top = 24.dp)
            )
        }
    }
}

fun getColorForContributions(contributions: Int): Color {
    return when (contributions) {
        0 -> githubContributionColor5
        in 1..5 -> githubContributionColor4
        in 6..10 -> githubContributionColor3
        in 11..18 -> githubContributionColor2
        else -> githubContributionColor1
    }
}
