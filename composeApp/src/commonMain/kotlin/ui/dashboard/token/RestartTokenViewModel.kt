package ui.dashboard.token

import androidx.lifecycle.ViewModel
import crypt.CryptoHandler
import repository.PullRequestRepository

class RestartTokenViewModel(
    private val cryptoHandler: CryptoHandler,
    private val repository: PullRequestRepository
) : ViewModel() {
    fun updateRepositoryToken(repositoryId: Int, newToken: String) {
        val encryptedToken = cryptoHandler.encrypt(newToken)
        repository.clearRepositoryPullRequest(repositoryId)
        repository.updateRepositoryToken(repositoryId, encryptedToken)
    }
}
