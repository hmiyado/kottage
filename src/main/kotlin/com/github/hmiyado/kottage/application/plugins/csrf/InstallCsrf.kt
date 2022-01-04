package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.HttpMethod

fun Application.csrf() {
    install(Csrf) {
        requestFilter { httpMethod, _ ->
            httpMethod in listOf(HttpMethod.Put, HttpMethod.Delete, HttpMethod.Post, HttpMethod.Patch)
        }
        header {
            validator { header, _ -> header == CustomHeaders.XCSRFToken }
            onFail { throw CsrfTokenException() }
        }
        session<ClientSession> {
            onFail {
                throw CsrfTokenException()
            }
        }
    }
}

class CsrfTokenException() : IllegalStateException()
