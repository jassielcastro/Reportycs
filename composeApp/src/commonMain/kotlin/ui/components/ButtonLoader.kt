package ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.GithubButtonOutlinedColor

@Composable
fun ButtonLoader(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
        onClick = onClick,
        colors = GithubButtonOutlinedColor(),
        modifier = modifier
            .fillMaxWidth(0.4f)
            .height(48.dp)
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn() + slideInVertically(animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = Spring.StiffnessLow
                ), initialOffsetY = { fullHeight -> fullHeight }) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp)
                )
            } else {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}
