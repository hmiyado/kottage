package com.github.hmiyado.application.configuration

import io.ktor.auth.UserPasswordCredential

data class AuthenticationConfiguration(
    val adminCredential: UserPasswordCredential
)
