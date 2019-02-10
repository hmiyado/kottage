package com.github.hmiyado

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging

@Suppress("unused") // Referenced in application.conf
fun Application.logging() {
    install(CallLogging)
}