package com.github.hmiyado.kottage.service.users

import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class Password(
    val value: String,
)

object PasswordGenerator {
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH = 512

    fun generateSecurePassword(
        rawPassword: String,
        salt: String,
    ): Password {
        val keySpec =
            PBEKeySpec(
                rawPassword.toCharArray(),
                getHashedSalt(salt),
                ITERATION_COUNT,
                KEY_LENGTH,
            )
        val secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)

        return secretKeyFactory
            .generateSecret(keySpec)
            .encoded
            .let(ByteArrayStringifier::stringify)
            .let { Password(it) }
    }

    private fun getHashedSalt(salt: String): ByteArray {
        // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
        val messageDigest = MessageDigest.getInstance("SHA-512")
        messageDigest.update(salt.toByteArray())
        return messageDigest.digest()
    }
}
