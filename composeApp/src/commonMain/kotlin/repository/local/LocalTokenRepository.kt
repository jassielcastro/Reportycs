package repository.local

import cache.DataBase
import crypt.CryptoHandler
import repository.mapper.toDto
import repository.model.TokenContributionDto

class LocalTokenRepository(
    private val dataBase: DataBase,
    private val cryptoHandler: CryptoHandler
) {
    /**
     * User contributions CRUD
     */

    fun addNewTokenForContributions(
        tokenName: String,
        token: String
    ) {
        val encryptedToken = cryptoHandler.encrypt(token)
        dataBase.addNewTokenForContributions(
            tokenName = tokenName,
            token = encryptedToken
        )
    }

    fun getAllTokenContributionList(): List<TokenContributionDto> {
        return dataBase.getTokenContributionList().map { entity ->
            entity.toDto()
        }
    }

    fun deleteTokenForContribution() {
        dataBase.deleteTokenForContributions()
    }
}
