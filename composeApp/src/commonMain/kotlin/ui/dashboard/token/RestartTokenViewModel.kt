package ui.dashboard.token

import androidx.lifecycle.ViewModel
import crypt.CryptoHandler
import usecase.PullRequestUseCase

class RestartTokenViewModel(
    private val cryptoHandler: CryptoHandler,
    private val repository: PullRequestUseCase
) : ViewModel() {
    fun updateRepositoryToken(repositoryId: Int, newToken: String) {
        val encryptedToken = cryptoHandler.encrypt(newToken)
        repository.clearRepositoryPullRequest(repositoryId)
        repository.updateRepositoryToken(repositoryId, encryptedToken)
    }
}
