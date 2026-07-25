package com.github.hmiyado.kottage.application.plugins.authentication

import io.ktor.util.NonceManager
import io.ktor.util.generateNonce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Encodes/decodes [PreOauthState] into a self-contained, HMAC-signed token that is embedded
 * directly in the OAuth `state` parameter sent to Google, instead of being looked up from a
 * server-side map (`pre-oauth-states`) keyed by an opaque state value.
 *
 * `state` is the only part of the OAuth2 authorization request that a spec-compliant provider is
 * guaranteed to echo back on the callback unchanged; any other data we want back has to ride
 * inside it. A process-local map cannot survive the authorize and callback requests landing on
 * two different Lambda execution environments, so the payload itself must be stateless.
 */
class OauthStateCodec(
    private val signKey: ByteArray,
) {
    private val base64Encoder = Base64.getUrlEncoder().withoutPadding()
    private val base64Decoder = Base64.getUrlDecoder()

    fun encode(state: PreOauthState): String {
        val payload = base64Encoder.encodeToString(Json.encodeToString(state).toByteArray(Charsets.UTF_8))
        val signature = base64Encoder.encodeToString(hmac(payload))
        return "$payload.$signature"
    }

    /**
     * Verifies the signature and expiration of [token] and decodes it back into a [PreOauthState].
     * Returns null if the token is malformed, was not signed with [signKey], or has expired.
     */
    fun decode(token: String): PreOauthState? {
        val separatorIndex = token.lastIndexOf('.')
        if (separatorIndex <= 0 || separatorIndex == token.length - 1) return null
        val payload = token.substring(0, separatorIndex)
        val signature = token.substring(separatorIndex + 1)
        val expectedSignature = base64Encoder.encodeToString(hmac(payload))
        if (!constantTimeEquals(signature, expectedSignature)) return null
        val state =
            runCatching {
                Json.decodeFromString<PreOauthState>(String(base64Decoder.decode(payload), Charsets.UTF_8))
            }.getOrNull() ?: return null
        if (state.expiresAt < System.currentTimeMillis()) return null
        return state
    }

    private fun hmac(data: String): ByteArray {
        val mac = Mac.getInstance(ALGORITHM)
        mac.init(SecretKeySpec(signKey, ALGORITHM))
        return mac.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    private fun constantTimeEquals(
        a: String,
        b: String,
    ): Boolean = MessageDigest.isEqual(a.toByteArray(Charsets.UTF_8), b.toByteArray(Charsets.UTF_8))

    private companion object {
        const val ALGORITHM = "HmacSHA256"
    }
}

/**
 * Adapts [OauthStateCodec] to Ktor's OAuth2 [NonceManager] contract so the `state` parameter's
 * signature and expiration are verified before an authorization code is exchanged for a token.
 *
 * [newNonce] is effectively unused: Ktor calls it with no request context to build the initial
 * `state` value, but `authorizeUrlInterceptor` (see `oidcGoogle`) overwrites that value with the
 * real signed token afterward, since only the interceptor has access to the current call. Only
 * [verifyNonce] matters here, and it must accept whatever the interceptor produced rather than
 * whatever [newNonce] returned.
 */
class OauthStateNonceManager(
    private val codec: OauthStateCodec,
) : NonceManager {
    override suspend fun newNonce(): String = generateNonce()

    override suspend fun verifyNonce(nonce: String): Boolean = codec.decode(nonce) != null
}
