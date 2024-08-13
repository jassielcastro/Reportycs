package ui.repositories.create

import androidx.lifecycle.ViewModel
import crypt.CryptoHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.PullRequestRepository
import repository.model.RepositoryData
import ui.model.GithubStats
import ui.model.UiState

class CreateNewRepositoryViewModel(
    private val cryptoHandler: CryptoHandler,
    private val repository: PullRequestRepository
) : ViewModel() {

    private val _saveRepositoryState: MutableStateFlow<UiState<String>> =
        MutableStateFlow(UiState.Idle)
    val saveRepoState = _saveRepositoryState.asStateFlow()

    suspend fun saveRepository(repositoryData: RepositoryData) {
        _saveRepositoryState.value = UiState.Loading
        val encryptedToken = cryptoHandler.encrypt(repositoryData.token)
        val repositoryEncrypted = repositoryData.copy(token = encryptedToken)
        repository.saveNewRepository(repositoryEncrypted)
        finisRepoConfiguration(repositoryData.repository)
    }

    private suspend fun finisRepoConfiguration(repositoryName: String) {
        val repo = repository.selectRepositoryBy(repositoryName) ?: return run {
            _saveRepositoryState.value = UiState.Failure
        }

        repository.setRepositoryMetrics(repo.id, 50)
        repository.getCodeOwners(repo)

        _saveRepositoryState.value = UiState.Success(repositoryName)
    }
}
