package ui.splash

import androidx.lifecycle.ViewModel
import repository.PullRequestRepository
import ui.GithubScreen

class SplashViewModel(
    private val repository: PullRequestRepository
) : ViewModel() {

    fun getRepositoriesDestinationScreen(): GithubScreen {
        val repos = repository.getAllRepositories()

        return if (repos.isEmpty()) {
            GithubScreen.CreateRepository
        } else {
            GithubScreen.Dashboard
        }
    }

    fun getUserDestinationScreen(): GithubScreen {
        return GithubScreen.UserDashboard
    }
}
