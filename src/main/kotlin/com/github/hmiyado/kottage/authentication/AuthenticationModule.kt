package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import io.ktor.sessions.SessionStorage
import java.time.Duration
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sessionExpiration: Duration = Duration.ofDays(7)

val authenticationModule = module {
    single<SessionStorage> {
        SessionStorageRedis(get(), "session", sessionExpiration)
    }
    single(named("admin")) { get<AuthenticationConfiguration>().adminCredential }
    single(named("user")) { get<AuthenticationConfiguration>().adminCredential }
}
