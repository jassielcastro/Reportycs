package usecase

import repository.model.ResponseStatus
import repository.remote.RemoteUserRepository
import usecase.mapper.toUserStaticData
import usecase.model.UserStaticsData

class UserContributionUseCase(
    private val remoteUserRepository: RemoteUserRepository,
) {
    suspend fun loadUserContributions(
        userName: String,
        from: String,
        to: String
    ): ResponseStatus<UserStaticsData> {
        val result = remoteUserRepository.loadUserContributions(
            userName = userName,
            from = from,
            to = to
        )
        return when (result) {
            is ResponseStatus.Error -> result

            is ResponseStatus.Success -> {
                ResponseStatus.Success(result.response.toUserStaticData())
            }
        }
    }
}
