package usecase

import repository.remote.RemoteUserRepository

class UserContributionUseCase(
    private val remoteUserRepository: RemoteUserRepository,
) {
    suspend fun loadUserContributions(
        token: String,
        userName: String
    ) {
        remoteUserRepository.loadUserContributions(token, userName)
    }
}
