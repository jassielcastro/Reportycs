package usecase

import repository.local.LocalUserRepository
import repository.remote.RemoteUserRepository
import usecase.mapper.toTokenData
import usecase.model.TokenForContributionData

class UserContributionUseCase(
    private val remoteUserRepository: RemoteUserRepository,
    private val localUserRepository: LocalUserRepository,
) {

    fun saveTokenToHandleContributions(
        token: String,
        tokenName: String
    ) {
        localUserRepository.addNewTokenForContributions(token, tokenName)
    }

    fun getAllTokensForContributions(): List<TokenForContributionData> {
        return localUserRepository.getAllTokenContributionList().map { token ->
            token.toTokenData()
        }
    }

    fun deleteToken(id: Int) {
        localUserRepository.deleteTokenForContribution(id)
    }

    suspend fun loadUserContributions(
        token: String,
        userName: String
    ) {
        remoteUserRepository.loadUserContributions(token, userName)
    }
}
