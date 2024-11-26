package usecase

import repository.model.ResponseStatus
import repository.remote.RemoteUserRepository
import repository.remote.model.response.GitHubContributionsResponse

class UserContributionUseCase(
    private val remoteUserRepository: RemoteUserRepository,
) {
    suspend fun loadUserContributions(
        userName: String
    ): ResponseStatus<GitHubContributionsResponse> {
        return remoteUserRepository.loadUserContributions(userName)
    }
}
