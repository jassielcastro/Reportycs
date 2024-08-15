package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.back_button
import jirareports.composeapp.generated.resources.ic_reportycs_logo_small
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.GithubScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubAppBar(
    currentScreen: GithubScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            AppBarLogo(
                showLogo = currentScreen != GithubScreen.Splash
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = if (currentScreen != GithubScreen.Splash) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.background
            },
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun AppBarLogo(
    modifier: Modifier = Modifier,
    showLogo: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxHeight(0.65f)
            .fillMaxWidth(0.1f)
    ) {
        AnimatedVisibility(
            visible = showLogo
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_reportycs_logo_small),
                contentDescription = "Image",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
