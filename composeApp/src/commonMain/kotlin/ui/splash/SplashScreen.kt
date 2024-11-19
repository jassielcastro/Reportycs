package ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.github_selector_repository_large_message
import jirareports.composeapp.generated.resources.github_selector_repository_message
import jirareports.composeapp.generated.resources.github_selector_repository_title
import jirareports.composeapp.generated.resources.github_selector_user_large_message
import jirareports.composeapp.generated.resources.github_selector_user_message
import jirareports.composeapp.generated.resources.github_selector_user_title
import jirareports.composeapp.generated.resources.ic_folder_git
import jirareports.composeapp.generated.resources.ic_users
import jirareports.composeapp.generated.resources.reportycs_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import ui.GithubScreen
import ui.components.dots.ConnectedDotsScreen
import ui.theme.dashboardColor

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateTo: (GithubScreen) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {

        ConnectedDotsScreen(
            modifier = Modifier
                .fillMaxSize(),
            dotsSize = 200,
            dimension = 2,
            dotColors = listOf(
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f)
            )
        )

        SplashRouterContainer(
            navigateTo = navigateTo
        )
    }
}

@Composable
fun SplashRouterContainer(
    durationMillisAnimation: Long = 2_000,
    navigateTo: (GithubScreen) -> Unit
) {
    var visible by remember {
        mutableStateOf(false)
    }
    val density = LocalDensity.current

    val viewModel = rememberKoinInject<SplashViewModel>()

    LaunchedEffect(key1 = true) {
        delay(timeMillis = durationMillisAnimation)
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(Res.drawable.reportycs_logo),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(0.35f),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        )

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CardComponentSelector(
                    modifier = Modifier
                        .requiredWidthIn(800.dp, 500.dp)
                        .wrapContentHeight(),
                    image = Res.drawable.ic_folder_git,
                    title = Res.string.github_selector_repository_title,
                    message = Res.string.github_selector_repository_message,
                    largeMessage = Res.string.github_selector_repository_large_message,
                    color = dashboardColor.copy(alpha = 0.55f),
                ) {
                    navigateTo(
                        viewModel.getRepositoriesDestinationScreen()
                    )
                }

                CardComponentSelector(
                    modifier = Modifier
                        .requiredWidthIn(800.dp, 500.dp)
                        .wrapContentHeight(),
                    image = Res.drawable.ic_users,
                    title = Res.string.github_selector_user_title,
                    message = Res.string.github_selector_user_message,
                    largeMessage = Res.string.github_selector_user_large_message,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
                ) {
                    /*
                    navigateTo(
                        viewModel.getRepositoriesDestinationScreen()
                    )
                     */
                }
            }
        }
    }
}

@Composable
private fun CardComponentSelector(
    modifier: Modifier,
    image: DrawableResource,
    title: StringResource,
    message: StringResource,
    largeMessage: StringResource,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(24.dp),
        shape = MaterialTheme.shapes.medium,
        color = color,
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Icon(
                    painter = painterResource(image),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(80.dp)
                        .padding(16.dp)
                )

                Text(
                    text = stringResource(title),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        lineHeight = 32.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Text(
                text = stringResource(message),
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                style = TextStyle(
                    lineHeight = 32.sp
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Text(
                text = stringResource(largeMessage),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                style = TextStyle(
                    lineHeight = 32.sp
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}
