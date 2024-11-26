package ui.user

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import jirareports.composeapp.generated.resources.users_dashboard_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import repository.remote.model.response.UserData
import ui.components.dots.ConnectedDotsScreen
import ui.model.UiState
import ui.repositories.TextPlaceHolder
import ui.theme.GithubTextOutlinedColor
import ui.theme.userSearchDotsColor
import ui.theme.userSearchDotsColor2

@Composable
fun UserDashboardScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        ConnectedDotsScreen(
            modifier = Modifier
                .fillMaxSize(),
            dotsSize = 200,
            dimension = 2,
            dotColors = listOf(
                userSearchDotsColor.copy(alpha = 0.05f),
                userSearchDotsColor2.copy(alpha = 0.03f)
            )
        )

        UserSearchScreen()
    }
}

@Composable
fun UserSearchScreen(modifier: Modifier = Modifier) {
    var usernameText by remember { mutableStateOf("") }
    val viewModel = rememberKoinInject<UserDashboardViewModel>()

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

        UserStatsScreen(
            modifier = Modifier
                .fillMaxSize(),
            viewModel = viewModel
        )
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
                    text = "",
                    message = stringResource(Res.string.users_dashboard_search_idle_state_message),
                )
            }

            UiState.NoInternet -> {
                UserMessageScreen(
                    text = "",
                    message = stringResource(Res.string.general_no_internet_error_message),
                )
            }

            is UiState.Success -> {
                UserStatsList(userData = state.data.data)
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
    userData: UserData
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Total of contributions: ${userData.user.contributionsCollection.contributionCalendar.totalContributions}",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(top = 24.dp)
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
