package repository.local

import cache.DataBase
import crypt.CryptoHandler
import repository.mapper.toDto
import repository.model.TokenContributionDto

class LocalUserRepository(
    private val dataBase: DataBase,
    private val cryptoHandler: CryptoHandler
) {
    /**
     * User contributions CRUD
     */

    fun addNewTokenForContributions(
        userName: String,
        token: String
    ) {
        val encryptedToken = cryptoHandler.encrypt(token)
        dataBase.addNewTokenForContributions(userName, encryptedToken)
    }

    fun getAllTokenContributionList(): List<TokenContributionDto> {
        return dataBase.getTokenContributionList().map { entity ->
            entity.toDto()
        }
    }

    fun deleteTokenForContribution(id: Int) {
        dataBase.deleteTokenForContributions(id)
    }
}
