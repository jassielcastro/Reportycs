package ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import usecase.PullRequestUseCase
import usecase.model.PullRequestData
import usecase.model.RepositoryData
import ui.model.UiState
import repository.model.ErrorStatus
import repository.model.ResponseStatus
import repository.remote.model.request.StatsRequest

class DashboardViewModel(
    private val useCase: PullRequestUseCase
) : ViewModel() {

    private val _repositoriesState: MutableStateFlow<UiState<List<RepositoryData>>> =
        MutableStateFlow(UiState.Idle)
    val repositoriesState = _repositoriesState.asStateFlow()

    private val _pullRequestState: MutableStateFlow<UiState<List<PullRequestData>>> =
        MutableStateFlow(UiState.Idle)
    val pullRequestState = _pullRequestState.asStateFlow()

    fun loadRepositories() {
        runCatching {
            useCase.getAllRepositories()
        }.onSuccess { repositories ->
            _repositoriesState.value = UiState.Success(repositories)
        }.onFailure {
            _repositoriesState.value = UiState.Failure
        }
    }

    suspend fun loadPullRequest(repositoryData: RepositoryData) {
        _pullRequestState.value = UiState.Loading

        runCatching {
            useCase.getPullRequest(
                repositoryData = repositoryData,
                statRequest = StatsRequest()
            )
        }.onSuccess { pullRequest ->
            handlePullRequestLoad(pullRequest)
        }.onFailure {
            it.printStackTrace()
            _pullRequestState.value = UiState.Failure
        }
    }

    private fun handlePullRequestLoad(status: ResponseStatus<List<PullRequestData>>) {
        when (status) {
            is ResponseStatus.Error -> {
                _pullRequestState.value = when (status.status) {
                    ErrorStatus.NO_INTERNET -> UiState.NoInternet
                    ErrorStatus.UNAUTHORIZED -> UiState.Unauthorized
                    ErrorStatus.EMPTY -> UiState.Success(emptyList())
                    else -> UiState.Failure
                }
            }

            is ResponseStatus.Success -> {
                _pullRequestState.value = UiState.Success(status.response)
            }
        }
    }

    fun loadPRsToAnalyze(repositoryId: Int): Int {
        return useCase.getPRsSizeToAnalyse(repositoryId)
    }

    fun deleteRepository(repositoryId: Int) {
        useCase.deleteRepository(repositoryId)
        loadRepositories()
    }
}
