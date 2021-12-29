package com.github.hmiyado.kottage.application.plugins.csrf

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.HttpMethod

fun Application.csrf() {
    install(Csrf) {
        requestFilter { httpMethod, _ ->
            httpMethod in listOf(HttpMethod.Put, HttpMethod.Delete, HttpMethod.Post, HttpMethod.Patch)
        }
        session<ClientSession> {
            onFail {
                throw CsrfTokenException()
            }
        }
    }
}

class CsrfTokenException() : IllegalStateException()
