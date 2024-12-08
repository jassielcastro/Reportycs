package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.general_retry_button
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.components.dots.ConnectedDotsScreen

@Composable
fun FailureScreen(
    modifier: Modifier,
    message: StringResource,
    retryMessage: StringResource = Res.string.general_retry_button,
    retry: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        /*ConnectedDotsScreen(
            modifier = Modifier
                .fillMaxSize(),
            dotsSize = 200,
            dimension = 2,
            dotColors = listOf(
                MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                MaterialTheme.colorScheme.error.copy(alpha = 0.03f)
            )
        )*/

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                text = stringResource(message),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    lineHeight = 42.sp
                )
            )

            NormalReportycsButton(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth()
                    .height(48.dp),
                text = stringResource(retryMessage),
                onClick = retry
            )
        }
    }
}
