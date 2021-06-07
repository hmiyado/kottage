package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.SessionStorageMemory
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authenticationModule = module {
    single<SessionStorage> { SessionStorageMemory() }
    single(named("admin")) { get<AuthenticationConfiguration>().adminCredential }
    single(named("user")) { get<AuthenticationConfiguration>().adminCredential }
}
