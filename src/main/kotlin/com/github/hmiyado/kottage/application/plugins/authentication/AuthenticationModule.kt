package com.github.hmiyado.kottage.application.plugins.authentication

import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.SessionStorageMemory
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Duration

val sessionExpiration: Duration = Duration.ofDays(7)

val authenticationModule = module {
    factory<SessionStorage> {
        SessionStorageMemory()
//        SessionStorageRedis(get(), "session", sessionExpiration)
    }
    single(named("oauth-redirects")) {
        mutableMapOf<String, String>()
    }
}
