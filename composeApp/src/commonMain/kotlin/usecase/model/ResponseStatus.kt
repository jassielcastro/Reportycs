package usecase.model

enum class ErrorStatus {
    NO_INTERNET,
    UNAUTHORIZED,
    EMPTY,
    ERROR,
    SERVER_ERROR,
    UNKNOWN
}

sealed class ResponseStatus<out T> {
    data class Success<T>(val response: T) : ResponseStatus<T>()
    data class Error(val status: ErrorStatus) : ResponseStatus<Nothing>()
}
