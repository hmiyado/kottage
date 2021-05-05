package com.github.hmiyado.kottage.application.configuration

import io.ktor.auth.UserPasswordCredential

data class AuthenticationConfiguration(
    val adminCredential: UserPasswordCredential
)
