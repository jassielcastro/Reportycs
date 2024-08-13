package ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.reportycs_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.rememberKoinInject
import ui.GithubScreen

@Composable
fun SplashScreen(
    durationMillisAnimation: Long = 1500,
    modifier: Modifier = Modifier,
    onFinish: (GithubScreen) -> Unit
) {
    val viewModel = rememberKoinInject<SplashViewModel>()

    LaunchedEffect(key1 = true) {
        delay(timeMillis = durationMillisAnimation)
        onFinish(viewModel.getDestinationScreen())
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painterResource(Res.drawable.reportycs_logo),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(0.35f)
        )
    }
}
