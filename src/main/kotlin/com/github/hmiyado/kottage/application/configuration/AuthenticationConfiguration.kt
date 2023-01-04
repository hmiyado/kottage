package com.github.hmiyado.kottage.application.configuration

import io.ktor.server.auth.UserPasswordCredential

data class AuthenticationConfiguration(
    val adminCredential: UserPasswordCredential,
)

data class OauthGoogle(
    val clientId: String,
    val clientSecret: String,
)
