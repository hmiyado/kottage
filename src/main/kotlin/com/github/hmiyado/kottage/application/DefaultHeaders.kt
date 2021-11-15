package com.github.hmiyado.kottage.application

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.DefaultHeaders

fun Application.defaultHeaders() {
    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
        header("X-XSS-Protection", "1; mode=block")
        header("X-Frame-Options", "deny")
        header("Content-Security-Policy", "default-src 'none'")
    }
}
