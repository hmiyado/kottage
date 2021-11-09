package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import io.ktor.sessions.SessionStorage
import java.time.Duration
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authenticationModule = module {
    single<SessionStorage> {
        SessionStorageRedis(get(), "session", Duration.ofDays(7))
    }
    single(named("admin")) { get<AuthenticationConfiguration>().adminCredential }
    single(named("user")) { get<AuthenticationConfiguration>().adminCredential }
}
