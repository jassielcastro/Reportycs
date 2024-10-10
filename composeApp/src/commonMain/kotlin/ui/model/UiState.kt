package ui.model

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data object Failure : UiState<Nothing>()
    data object NoInternet : UiState<Nothing>()
    data object Unauthorized : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
