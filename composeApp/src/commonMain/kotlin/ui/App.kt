package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import di.appModule
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.add_new_repository_screen
import jirareports.composeapp.generated.resources.splash_screen
import jirareports.composeapp.generated.resources.dashboard_screen
import jirareports.composeapp.generated.resources.statics_screen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import ui.components.GithubAppBar
import ui.dashboard.DashboardScreen
import ui.repositories.create.CreateNewRepositoryScreen
import ui.splash.SplashScreen
import ui.theme.GithubStatsTheme

/**
 * enum values that represent the screens in the app
 */
enum class GithubScreen(val title: StringResource) {
    Splash(title = Res.string.splash_screen),
    CreateRepository(title = Res.string.add_new_repository_screen),
    Dashboard(title = Res.string.dashboard_screen),
    Statics(title = Res.string.statics_screen)
}

@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController()
) {
    KoinApplication(application = {
        modules(appModule)
    }) {
        GithubStatsTheme {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentScreen = GithubScreen.valueOf(
                backStackEntry?.destination?.route ?: GithubScreen.Splash.name
            )

            Scaffold(
                topBar = {
                    GithubAppBar(
                        currentScreen = currentScreen,
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = GithubScreen.Splash.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(route = GithubScreen.Splash.name) {
                        SplashScreen { destination ->
                            navController.navigate(route = destination.name) {
                                popUpTo(route = GithubScreen.Splash.name) {
                                    inclusive = true
                                }
                            }
                        }
                    }

                    composable(route = GithubScreen.CreateRepository.name) {
                        CreateNewRepositoryScreen(
                            onSuccess = {
                                navController.navigate(route = GithubScreen.Dashboard.name) {
                                    popUpTo(route = GithubScreen.CreateRepository.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(route = GithubScreen.Dashboard.name) {
                        DashboardScreen()
                    }
                }
            }
        }
    }
}
