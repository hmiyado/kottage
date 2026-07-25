package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.SessionSignKey
import com.github.hmiyado.kottage.model.UserSession
import io.ktor.util.NonceManager
import org.koin.dsl.module
import java.time.Duration
import java.time.Instant

val sessionExpiration: Duration = Duration.ofDays(7)

// Matches the timeout previously passed to StatelessHmacNonceManager(timeoutMillis = 180_000).
val oauthStateExpiration: Duration = Duration.ofMinutes(3)

fun newUserSession(id: Long): UserSession =
    UserSession(
        id = id,
        // Absolute expiration, fixed at sign-in time and never refreshed: a sliding expiration
        // would let a leaked token stay valid indefinitely as long as it keeps being used.
        expiresAt = Instant.now().plus(sessionExpiration).toEpochMilli(),
    )

val authenticationModule =
    module {
        single {
            OauthStateCodec(get<SessionSignKey>().bytes)
        }
        single<NonceManager> {
            // Previously StatelessHmacNonceManager(key = generateNonce()...): a new random key
            // per process, so an OAuth flow that started before a restart/recycle and completed
            // after it would always fail nonce verification. Using SESSION_SIGN_KEY fixes that
            // (a real bug, not just a Lambda-readiness concern) as a side effect of going
            // stateless.
            OauthStateNonceManager(get())
        }
    }
