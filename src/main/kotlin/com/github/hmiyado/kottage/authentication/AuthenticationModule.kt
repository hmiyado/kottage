package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authenticationModule = module {
    single(named("admin")) { get<AuthenticationConfiguration>().adminCredential }
}
