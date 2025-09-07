package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.csrf.CSRF
import io.ktor.server.plugins.origin
import org.koin.ktor.ext.get

fun Application.csrf() {
    install(CSRF) {
        // tests Origin is an expected value
        val origin = if (this@csrf.get<DevelopmentConfiguration>() == DevelopmentConfiguration.Production) {
            "https://miyado.dev"
        } else {
            "http://localhost:3000"
        }
        allowOrigin(origin)

        // tests Origin matches Host header
        originMatchesHost()

        // custom header checks
        checkHeader(CustomHeaders.XCSRFToken.uppercase())

        onFailure {
            if(!request.headers.contains(CustomHeaders.XCSRFToken.uppercase())) {
                throw CsrfHeaderException()
            }
            if(request.origin.uri != origin) {
                throw CsrfOriginException()
            }
        }
    }
}

class CsrfOriginException : IllegalStateException()

class CsrfHeaderException : IllegalStateException()
