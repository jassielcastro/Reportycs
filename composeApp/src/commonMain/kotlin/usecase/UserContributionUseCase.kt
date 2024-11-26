package usecase

import repository.remote.RemoteUserRepository

class UserContributionUseCase(
    private val remoteUserRepository: RemoteUserRepository,
) {
    suspend fun loadUserContributions(userName: String) {
        remoteUserRepository.loadUserContributions(userName)
    }
}
