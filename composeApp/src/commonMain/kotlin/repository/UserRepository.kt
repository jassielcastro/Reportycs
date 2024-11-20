package repository

import usecase.remote.RemoteUserUseCase

class UserRepository(
    private val userUseCase: RemoteUserUseCase
) {
    suspend fun loadUserRepositories(
        token: String,
        userName: String
    ) {
        userUseCase.loadUserRepositories(token, userName)
    }
}
