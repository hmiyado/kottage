package com.github.hmiyado.route

import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.basic

fun Authentication.Configuration.admin(adminCredential: UserPasswordCredential) {
    basic {
        validate { credential ->
            if (credential == adminCredential) {
                UserIdPrincipal(credential.name)
            } else {
                null
            }
        }
    }
}
