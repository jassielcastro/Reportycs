package ui.dashboard.token

import androidx.lifecycle.ViewModel
import usecase.PullRequestUseCase

class RestartTokenViewModel(
    private val repository: PullRequestUseCase
) : ViewModel() {
    fun updateRepositoryToken(repositoryId: Int, newToken: String) {
        repository.clearRepositoryPullRequest(repositoryId)
        repository.updateRepositoryToken(repositoryId, newToken)
    }
}
