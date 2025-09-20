package com.github.hmiyado.kottage.application.plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders

fun Application.defaultHeaders() {
    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
        header("X-XSS-Protection", "1; mode=block")
        header("X-Frame-Options", "deny")
        header("Content-Security-Policy", "default-src 'none'")
        header(HttpHeaders.Server, "Ktor")
    }
}

object CustomHeaders {
    const val X_CSRF_TOKEN: String = "X-CSRF-Token"
}
