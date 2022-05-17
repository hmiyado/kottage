package com.github.hmiyado.kottage.application.plugins.authentication

import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.SessionStorageMemory
import java.time.Duration
import org.koin.dsl.module

val sessionExpiration: Duration = Duration.ofDays(7)

val authenticationModule = module {
    single<SessionStorage> {
        SessionStorageMemory()
//        SessionStorageRedis(get(), "session", sessionExpiration)
    }
}
