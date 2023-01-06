package com.github.hmiyado.kottage.application.plugins.authentication

/**
 * @param redirectUrl specified redirect url
 * @param userId userId or null if not signed in
 */
data class PreOauthState(
    val redirectUrl: String,
    val userId: Long?,
)
