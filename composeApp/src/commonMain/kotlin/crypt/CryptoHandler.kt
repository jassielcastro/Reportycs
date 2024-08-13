package crypt

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

class CryptoHandler(
    private val secret: String
) {

    private val secretKeySpec: SecretKeySpec by lazy {
        SecretKeySpec(secret.toByteArray(), ALGORITHM)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(CIPHER)

        val iv = ByteArray(BYTE_SIZE).apply { Random.nextBytes(this) }
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)

        val encryptedBytes = cipher.doFinal(data.encodeToByteArray())
        val combined = iv + encryptedBytes
        return Base64.encode(combined)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(data: String): String {
        val combined = Base64.decode(data)
        val iv = combined.copyOfRange(0, BYTE_SIZE)
        val encryptedBytes = combined.copyOfRange(BYTE_SIZE, combined.size)

        val cipher = Cipher.getInstance(CIPHER)
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return decryptedBytes.decodeToString()
    }

    private companion object {
        const val ALGORITHM = "AES"
        const val CIPHER = "AES/CBC/PKCS5Padding"
        const val BYTE_SIZE = 16
    }
}
