package ui.user

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import repository.model.ErrorStatus
import repository.model.ResponseStatus
import ui.model.UiState
import usecase.UserContributionUseCase
import usecase.model.UserStaticsData

class UserDashboardViewModel(
    private val userContributionUseCase: UserContributionUseCase,
    private val searchDelay: Long = 1_500L
) : ViewModel() {

    private val _username = MutableStateFlow("")

    private val _userStats: MutableStateFlow<UiState<UserStaticsData>> =
        MutableStateFlow(UiState.Idle)
    val userStats: StateFlow<UiState<UserStaticsData>> = _userStats.asStateFlow()

    @OptIn(FlowPreview::class)
    suspend fun initUserNameListener() {
        _username
            .debounce(searchDelay)
            .distinctUntilChanged()
            .collect { username ->
                loadUserContributionFor(username)
            }
    }

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    private suspend fun loadUserContributionFor(
        username: String
    ) {
        if (username.isEmpty()) {
            _userStats.value = UiState.Idle
            return
        }

        _userStats.value = UiState.Loading
        when (val result = userContributionUseCase.loadUserContributions(username)) {
            is ResponseStatus.Error -> {
                handleError(result.status)
            }

            is ResponseStatus.Success -> {
                _userStats.value = UiState.Success(result.response)
            }
        }
    }

    private fun handleError(status: ErrorStatus) {
        _userStats.value = when (status) {
            ErrorStatus.NO_INTERNET -> UiState.NoInternet
            ErrorStatus.UNAUTHORIZED -> UiState.Unauthorized
            else -> UiState.Failure
        }
    }
}
