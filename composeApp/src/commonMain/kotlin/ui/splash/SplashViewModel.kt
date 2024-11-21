package ui.splash

import androidx.lifecycle.ViewModel
import usecase.PullRequestUseCase
import ui.GithubScreen

class SplashViewModel(
    private val repository: PullRequestUseCase
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
