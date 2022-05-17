package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import io.github.hmiyado.ktor.csrfprotection.Csrf
import io.github.hmiyado.ktor.csrfprotection.header
import io.github.hmiyado.ktor.csrfprotection.session
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.ext.get

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
        header {
            validator { headers -> headers.entries().any { (k, _) -> k.uppercase() == CustomHeaders.XCSRFToken.uppercase() } }
            onFail { throw CsrfHeaderException() }
        }
        header {
            validator { headers ->
                val origin = if (get<DevelopmentConfiguration>() == DevelopmentConfiguration.Production) {
                    "https://miyado.dev"
                } else {
                    "http://localhost:3000"
                }
                if (!headers.contains(HttpHeaders.Origin) && !headers.contains(HttpHeaders.Referrer)) {
                    return@validator false
                }
                if (headers.contains(HttpHeaders.Origin)) {
                    return@validator headers[HttpHeaders.Origin]?.contains(origin) == true
                }
                if (headers.contains(HttpHeaders.Referrer)) {
                    return@validator headers[HttpHeaders.Referrer]?.contains(origin) == true
                }
                true
            }
            onFail {
                throw CsrfOriginException()
            }
        }
    }
}

class CsrfOriginException : IllegalStateException()

class CsrfHeaderException : IllegalStateException()

class CsrfTokenException : IllegalStateException()
