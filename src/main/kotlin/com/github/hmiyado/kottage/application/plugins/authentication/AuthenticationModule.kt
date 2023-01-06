package com.github.hmiyado.kottage.application.plugins.authentication

import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.util.NonceManager
import io.ktor.util.StatelessHmacNonceManager
import io.ktor.util.generateNonce
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Duration

val sessionExpiration: Duration = Duration.ofDays(7)

val authenticationModule = module {
    factory<SessionStorage> {
        SessionStorageMemory()
//        SessionStorageRedis(get(), "session", sessionExpiration)
    }
    single(named("pre-oauth-states")) {
        mutableMapOf<String, PreOauthState>()
    }
    single<NonceManager> {
        StatelessHmacNonceManager(
            key = generateNonce().toByteArray(),
            timeoutMillis = 180_000,
        )
    }
}
