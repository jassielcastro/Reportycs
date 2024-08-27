package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import ui.components.GithubAppBar
import ui.dashboard.DashboardScreen
import ui.repositories.CreateNewRepositoryScreen
import ui.splash.SplashScreen
import ui.statics.StaticsScreen
import ui.theme.GithubStatsTheme

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

    companion object {
        fun valueOf(destination: String): GithubScreen {
            return when (destination) {
                CreateRepository.destination -> CreateRepository
                Dashboard.destination -> Dashboard
                Statics.destination -> Statics
                else -> Splash
            }
        }
    }
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
                backStackEntry?.destination?.route ?: GithubScreen.Splash.destination
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
                    startDestination = GithubScreen.Splash.destination,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(route = GithubScreen.Splash.destination) {
                        SplashScreen { destination ->
                            navController.navigate(route = destination.destination) {
                                popUpTo(route = GithubScreen.Splash.destination) {
                                    inclusive = true
                                }
                            }
                        }
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
                            onGenerateReports = { repo ->
                                navController.navigate(
                                    route = GithubScreen.Statics.withArgs(repo.repository)
                                )
                            },
                            addNewRepository = {
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
                }
            }
        }
    }
}
