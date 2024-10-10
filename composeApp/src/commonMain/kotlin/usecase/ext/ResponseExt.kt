package usecase.ext

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import usecase.model.ErrorStatus
import usecase.model.ResponseStatus

suspend inline fun <reified T> HttpResponse.handleResponse(): ResponseStatus<T> {
    return try {
        when (this.status.value) {
            in 200..299 -> ResponseStatus.Success(this.body<T>())
            401 -> ResponseStatus.Error(ErrorStatus.UNAUTHORIZED)
            in 400..499 -> ResponseStatus.Error(ErrorStatus.ERROR)
            in 500..599 -> ResponseStatus.Error(ErrorStatus.SERVER_ERROR)
            -1000 -> ResponseStatus.Error(ErrorStatus.NO_INTERNET)
            else -> {
                ResponseStatus.Error(ErrorStatus.UNKNOWN)
            }
        }
    } catch (ignore: Exception) {
        ResponseStatus.Error(ErrorStatus.UNKNOWN)
    }
}
