package usecase

import repository.remote.RemoteUserRepository

class UserUseCase(
    private val userUseCase: RemoteUserRepository
) {
    suspend fun loadUserRepositories(
        token: String,
        userName: String
    ) {
        userUseCase.loadUserRepositories(token, userName)
    }
}
