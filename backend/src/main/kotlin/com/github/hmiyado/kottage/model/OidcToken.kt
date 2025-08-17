package com.github.hmiyado.kottage.model

import java.time.ZonedDateTime

/**
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">link</a>
 */
data class OidcToken(
    val issuer: String,
    val subject: String,
    val audience: String,
    val expiration: ZonedDateTime,
    val issuedAt: ZonedDateTime,
)
