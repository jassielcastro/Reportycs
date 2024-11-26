package usecase

import repository.model.ResponseStatus
import repository.remote.RemoteUserRepository
import usecase.mapper.toUserStaticData
import usecase.model.UserStaticsData

class UserContributionUseCase(
    private val remoteUserRepository: RemoteUserRepository,
) {
    suspend fun loadUserContributions(
        userName: String
    ): ResponseStatus<UserStaticsData> {
        return when (val result = remoteUserRepository.loadUserContributions(userName)) {
            is ResponseStatus.Error -> result

            is ResponseStatus.Success -> {
                ResponseStatus.Success(result.response.toUserStaticData())
            }
        }
    }
}
