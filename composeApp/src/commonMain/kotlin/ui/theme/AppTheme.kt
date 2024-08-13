package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    errorContainer = onErrorLight,
    onError = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    inverseOnSurface = outlineVariantLight,
    inverseSurface = scrimLight,
    inversePrimary = inverseSurfaceLight,
    surfaceTint = inverseOnSurfaceLight,
    outlineVariant = inversePrimaryLight,
    scrim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainer = surfaceContainerLowestLight,
    surfaceContainerHigh = surfaceContainerLowLight,
    surfaceContainerHighest = surfaceContainerLight,
    surfaceContainerLow = surfaceContainerHighLight,
    surfaceContainerLowest = surfaceContainerHighestLight,
)

@Composable
fun GithubStatsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
