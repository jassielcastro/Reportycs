package repository.remote

import crypt.CryptoHandler
import repository.local.LocalTokenRepository

interface TokenHandlerDelegate {

    fun getDecryptedToken(): String

    class Impl(
        private val localTokenRepository: LocalTokenRepository,
        private val cryptoHandler: CryptoHandler,
    ) : TokenHandlerDelegate {
        override fun getDecryptedToken(): String {
            val token = localTokenRepository
                .getAllTokenContributionList()
                .map { it.token }
                .firstOrNull()
                .orEmpty()
            return cryptoHandler.decrypt(token)
        }
    }
}
