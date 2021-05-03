package com.github.hmiyado.authentication

import com.github.hmiyado.application.AuthenticationConfiguration
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authenticationModule = module {
    single(named("admin")) { get<AuthenticationConfiguration>().adminCredential }
}
