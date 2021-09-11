package com.github.hmiyado.kottage.application

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json

fun Application.contentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}
