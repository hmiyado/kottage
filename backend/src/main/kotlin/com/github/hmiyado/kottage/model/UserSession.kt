package com.github.hmiyado.kottage.model

import kotlinx.serialization.Serializable

/**
 * @param expiresAt epoch millis after which this session must be treated as unauthenticated.
 * Signed together with [id] so a client cannot extend its own session by tampering with the
 * cookie's `maxAge`, and so a stolen-but-not-yet-expired token cannot be replayed forever now
 * that no server-side store can revoke it.
 */
@Serializable
data class UserSession(
    val id: Long = 0,
    val expiresAt: Long = 0,
)
