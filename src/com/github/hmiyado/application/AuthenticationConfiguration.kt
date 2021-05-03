package com.github.hmiyado.application

import io.ktor.auth.UserPasswordCredential

data class AuthenticationConfiguration(
    val adminCredential: UserPasswordCredential
)
