package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.loading_info_message_loading
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.components.dots.ConnectedDotsScreen

@Composable
fun LoadingScreen(
    modifier: Modifier,
    loadingText: List<StringResource>,
    displayTime: Long = 1_500L
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        ConnectedDotsScreen(
            modifier = Modifier
                .fillMaxSize(),
            dotsSize = 300,
            dimension = 1,
            dotColors = listOf(
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
            )
        )

        LoadingText(loadingText, displayTime)
    }
}

@Composable
private fun LoadingText(
    loadingText: List<StringResource>,
    displayTime: Long = 2_500L
) {
    var currentItemIndex by remember { mutableStateOf(0) }

    LaunchedEffect(currentItemIndex) {
        delay(displayTime)
        currentItemIndex = (currentItemIndex + 1) % loadingText.size
    }

    val currentItem = loadingText[currentItemIndex]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.loading_info_message_loading),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier
                .size(14.dp)
        )

        Text(
            text = stringResource(currentItem),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
