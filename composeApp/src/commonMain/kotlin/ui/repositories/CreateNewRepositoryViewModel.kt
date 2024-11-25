package ui.repositories

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ui.model.UiState
import usecase.PullRequestUseCase
import usecase.model.RepositoryData

class CreateNewRepositoryViewModel(
    private val repository: PullRequestUseCase
) : ViewModel() {

    private val _saveRepositoryState: MutableStateFlow<UiState<String>> =
        MutableStateFlow(UiState.Idle)
    val saveRepoState = _saveRepositoryState.asStateFlow()

    private val _createState: MutableStateFlow<CreateState> =
        MutableStateFlow(CreateState())
    val createState = _createState.asStateFlow()

    fun saveRepository(repositoryData: RepositoryData, codeOwners: String) {
        if (repositoryData.owner.isEmpty()) {
            _createState.value = _createState.value.copy(hasOwnerError = true)
        }

        if (repositoryData.repository.isEmpty()) {
            _createState.value = _createState.value.copy(hasNameError = true)
        }

        if (codeOwners.isEmpty()) {
            _createState.value = _createState.value.copy(hasCodeOwnersError = true)
        }

        if (!_createState.value.hasAnyError()) {
            _saveRepositoryState.value = UiState.Loading
            repository.saveNewRepository(repositoryData)
            finisRepoConfiguration(repositoryData.repository, codeOwners)
        }
    }

    private fun finisRepoConfiguration(repositoryName: String, codeOwners: String) {
        val repo = repository.selectRepositoryBy(repositoryName) ?: return run {
            _saveRepositoryState.value = UiState.Failure
        }

        repository.setRepositoryMetrics(repo.id, 100)

        val owners = generateOwners(codeOwners)

        repository.addCodeOwners(owners, repo)

        _saveRepositoryState.value = UiState.Success(repositoryName)
    }

    private fun generateOwners(codeOwners: String): List<String> {
        return codeOwners.trim()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun updateOwnerErrorState(newValue: Boolean) {
        _createState.value = _createState.value.copy(hasOwnerError = newValue)
    }

    fun updateNameErrorState(newValue: Boolean) {
        _createState.value = _createState.value.copy(hasNameError = newValue)
    }

    fun updateCodeOwnerErrorState(newValue: Boolean) {
        _createState.value = _createState.value.copy(hasCodeOwnersError = newValue)
    }

    data class CreateState(
        var hasOwnerError: Boolean = false,
        var hasNameError: Boolean = false,
        var hasCodeOwnersError: Boolean = false
    ) {
        fun hasAnyError() = hasOwnerError || hasNameError || hasCodeOwnersError
    }
}
