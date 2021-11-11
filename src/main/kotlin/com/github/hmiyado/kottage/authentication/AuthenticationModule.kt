package com.github.hmiyado.kottage.authentication

import io.ktor.sessions.SessionStorage
import java.time.Duration
import org.koin.dsl.module

val sessionExpiration: Duration = Duration.ofDays(7)

val authenticationModule = module {
    single<SessionStorage> {
        SessionStorageRedis(get(), "session", sessionExpiration)
    }
}
