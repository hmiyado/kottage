package com.github.hmiyado.authentication

import io.ktor.auth.UserPasswordCredential
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authenticationModule = module {
    single(named("admin")) {
        UserPasswordCredential(
            System.getenv("ADMIN_NAME"),
            System.getenv("ADMIN_PASSWORD")
        )
    }
}
