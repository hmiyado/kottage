package com.github.hmiyado.kottage.application.plugins.authentication

import kotlinx.serialization.Serializable

/**
 * @param redirectUrl specified redirect url
 * @param userId userId or null if not signed in
 * @param nonce nonce passed to Google and later checked against the OIDC id token's nonce claim
 * @param expiresAt epoch millis after which this state must be rejected, preventing an old
 * `state` value (and the authorize URL it was embedded in) from being replayed
 */
@Serializable
data class PreOauthState(
    val redirectUrl: String,
    val userId: Long?,
    val nonce: String,
    val expiresAt: Long,
)
