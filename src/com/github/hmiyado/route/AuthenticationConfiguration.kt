package com.github.hmiyado.route

import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic

fun Authentication.Configuration.admin() {
    basic {
        validate { (name, password) ->
            if (name == "admin" && password == "admin") {
                UserIdPrincipal(name)
            } else {
                null
            }
        }
    }
}
