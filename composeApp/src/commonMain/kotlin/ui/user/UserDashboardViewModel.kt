package ui.user

import androidx.lifecycle.ViewModel
import ext.aMonthAgo
import ext.aWeekAgo
import ext.aYearAgo
import ext.formatAsGithub
import ext.now
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import repository.model.ErrorStatus
import repository.model.ResponseStatus
import ui.model.TimePeriod
import ui.model.UiState
import usecase.UserContributionUseCase
import usecase.model.UserStaticsData

class UserDashboardViewModel(
    private val userContributionUseCase: UserContributionUseCase,
    private val searchDelay: Long = 800L
) : ViewModel() {

    private val _username = MutableStateFlow("")
    private var selectedTimePeriod: TimePeriod = TimePeriod.YEAR

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

    suspend fun onPeriodSelected(selectedTimePeriod: TimePeriod) {
        if (this.selectedTimePeriod == selectedTimePeriod) return
        this.selectedTimePeriod = selectedTimePeriod
        if (_username.value.isNotEmpty()) {
            loadUserContributionFor(_username.value)
        }
    }

    private suspend fun loadUserContributionFor(
        username: String
    ) {
        if (username.isEmpty()) {
            _userStats.value = UiState.Idle
            return
        }

        val now = now().formatAsGithub()
        val from = getDateFromPeriod()

        _userStats.value = UiState.Loading

        val result = userContributionUseCase.loadUserContributions(
            userName = username,
            from = from,
            to = now
        )
        when (result) {
            is ResponseStatus.Error -> {
                handleError(result.status)
            }

            is ResponseStatus.Success -> {
                _userStats.value = UiState.Success(result.response)
            }
        }
    }

    private fun getDateFromPeriod(): String {
        return when (selectedTimePeriod) {
            TimePeriod.YEAR -> aYearAgo()
            TimePeriod.MONTH -> aMonthAgo()
            TimePeriod.WEEK -> aWeekAgo()
        }.formatAsGithub()
    }

    private fun handleError(status: ErrorStatus) {
        _userStats.value = when (status) {
            ErrorStatus.NO_INTERNET -> UiState.NoInternet
            ErrorStatus.UNAUTHORIZED -> UiState.Unauthorized
            else -> UiState.Failure
        }
    }
}
