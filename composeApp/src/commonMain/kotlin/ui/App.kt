package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import di.appModule
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import ui.components.GithubAppBar
import ui.components.dots.ConnectedDotsScreen
import ui.dashboard.DashboardScreen
import ui.repositories.CreateNewRepositoryScreen
import ui.splash.SplashScreen
import ui.statics.StaticsScreen
import ui.theme.GithubStatsTheme
import ui.theme.dashboardColor
import ui.token.RestartTokenScreen
import ui.user.UserDashboardScreen

/**
 * enum values that represent the screens in the app
 */
sealed class GithubScreen(
    val destination: String,
    val arguments: List<NamedNavArgument> = listOf()
) {
    data object Splash : GithubScreen(destination = "splash")

    data object CreateRepository : GithubScreen(destination = "createRepository")

    data object Dashboard : GithubScreen(destination = "dashboard")

    data object Statics : GithubScreen(
        destination = "statics/{repositoryName}",
        arguments = listOf(navArgument("repositoryName") { type = NavType.StringType })
    ) {
        fun withArgs(repositoryName: String): String {
            return "statics/$repositoryName"
        }
    }

    data object UserDashboard : GithubScreen(destination = "user_dashboard")

    companion object {
        fun valueOf(destination: String): GithubScreen {
            return when (destination) {
                CreateRepository.destination -> CreateRepository
                Dashboard.destination -> Dashboard
                Statics.destination -> Statics
                UserDashboard.destination -> UserDashboard
                else -> Splash
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController(),
) {
    KoinApplication(application = {
        modules(appModule)
    }) {
        GithubStatsTheme {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentScreen = GithubScreen.valueOf(
                backStackEntry?.destination?.route ?: GithubScreen.Splash.destination
            )

            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            var showBottomSheet by remember { mutableStateOf(false) }
            var reloadState by remember { mutableStateOf(false) }

            Scaffold(
                topBar = {
                    GithubAppBar(
                        currentScreen = currentScreen,
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        onUpdateTokenClick = {
                            showBottomSheet = true
                        }
                    )
                }
            ) { innerPadding ->
                ConnectedDotsScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                    dotsCount = 300,
                    dotColors = listOf(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.07f),
                        dashboardColor.copy(alpha = 0.055f)
                    )
                )

                NavHost(
                    navController = navController,
                    startDestination = GithubScreen.Splash.destination,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(route = GithubScreen.Splash.destination) {
                        SplashScreen(
                            reloadState = reloadState,
                            navigateTo = { destination ->
                                navController.navigate(route = destination.destination)
                            },
                            onRequestAddTokenScreen = {
                                showBottomSheet = true
                            }
                        )
                    }

                    composable(route = GithubScreen.CreateRepository.destination) {
                        CreateNewRepositoryScreen(
                            onSuccess = {
                                navController.navigate(route = GithubScreen.Dashboard.destination) {
                                    popUpTo(route = GithubScreen.CreateRepository.destination) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(route = GithubScreen.Dashboard.destination) {
                        DashboardScreen(
                            navigateToGenerateReports = { repo ->
                                navController.navigate(
                                    route = GithubScreen.Statics.withArgs(repo.repository)
                                )
                            },
                            navigateToAddNewRepository = {
                                navController.navigate(
                                    route = GithubScreen.CreateRepository.destination
                                )
                            }
                        )
                    }

                    composable(
                        route = GithubScreen.Statics.destination,
                        arguments = GithubScreen.Statics.arguments
                    ) { entry ->
                        val repositoryName = entry.arguments?.getString("repositoryName").orEmpty()
                        StaticsScreen(
                            repositoryName = repositoryName
                        )
                    }

                    composable(route = GithubScreen.UserDashboard.destination) {
                        UserDashboardScreen()
                    }
                }

                if (showBottomSheet) {
                    reloadState = false
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        sheetState = sheetState
                    ) {
                        RestartTokenScreen {
                            scope.launch {
                                reloadState = true
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
