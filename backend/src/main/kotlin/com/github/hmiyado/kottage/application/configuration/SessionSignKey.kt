package com.github.hmiyado.kottage.application.configuration

/**
 * The HMAC key used to sign stateless sessions (cookie<UserSession>/cookie<ClientSession>/
 * header<CsrfTokenSession>), the stateless OAuth `state` token, and the OAuth nonce manager.
 *
 * Sessions used to be kept in server-side memory (`SessionStorageMemory`), so cookies only ever
 * carried an opaque, meaningless id and signing was unnecessary. Once the app runs on Lambda,
 * multiple execution environments can be involved within a single session's lifetime, so the
 * cookie itself must become the credential. Without a signature, a client could forge or tamper
 * with it; without a mandatory key, a misconfigured production deployment would silently accept
 * an insecure default. Hence this type fails fast instead of falling back to a default value.
 */
class SessionSignKey(
    val bytes: ByteArray,
) {
    companion object {
        /**
         * 128 bit (32 hex chars) is the practical floor for an HMAC-SHA256 key. The documented
         * generation command (`openssl rand -hex 32`) produces 256 bit (64 hex chars); this is
         * intentionally looser so it only rejects keys that are clearly too weak.
         */
        const val MIN_HEX_LENGTH = 32

        fun fromHex(hex: String?): SessionSignKey {
            val value = hex?.trim()
            if (value.isNullOrEmpty()) {
                throw SessionSignKeyConfigurationException(
                    "SESSION_SIGN_KEY is not set. Generate one with `openssl rand -hex 32` and set it as an environment variable.",
                )
            }
            if (value.length < MIN_HEX_LENGTH) {
                throw SessionSignKeyConfigurationException(
                    "SESSION_SIGN_KEY is too short (${value.length} hex chars, minimum is $MIN_HEX_LENGTH). " +
                        "Generate one with `openssl rand -hex 32`.",
                )
            }
            val bytes =
                try {
                    decodeHex(value)
                } catch (e: IllegalArgumentException) {
                    throw SessionSignKeyConfigurationException("SESSION_SIGN_KEY must be a hex string.", e)
                }
            return SessionSignKey(bytes)
        }

        private fun decodeHex(hex: String): ByteArray {
            require(hex.length % 2 == 0) { "hex string must have an even length" }
            return ByteArray(hex.length / 2) { i ->
                val index = i * 2
                hex.substring(index, index + 2).toInt(16).toByte()
            }
        }
    }
}

class SessionSignKeyConfigurationException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
