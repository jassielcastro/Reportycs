package usecase

import repository.local.LocalTokenRepository
import usecase.mapper.toTokenData
import usecase.model.TokenForContributionData

class TokenUseCase(
    private val localUserRepository: LocalTokenRepository,
) {

    fun saveTokenToHandleContributions(
        token: String,
        tokenName: String
    ) {
        localUserRepository.deleteTokenForContribution()
        localUserRepository.addNewTokenForContributions(
            tokenName = tokenName,
            token = token,
        )
    }

    fun getAllTokensForContributions(): List<TokenForContributionData> {
        return localUserRepository.getAllTokenContributionList().map { token ->
            token.toTokenData()
        }
    }
}
