package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.HttpMethod
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
                if (get<DevelopmentConfiguration>() != DevelopmentConfiguration.Production) {
                    return@validator true
                }
                if (headers.contains("Origin") && headers.contains("Referer")) {
                    return@validator false
                }
                if (headers.contains("Origin")) {
                    return@validator headers["Origin"]?.contains("https://miyado.dev") == true
                }
                if (headers.contains("Referer")) {
                    return@validator headers["Referer"]?.contains("https://miyado.dev") == true
                }
                true
            }
            onFail { throw CsrfOriginException() }
        }
    }
}

class CsrfOriginException : IllegalStateException()

class CsrfHeaderException : IllegalStateException()

class CsrfTokenException : IllegalStateException()
