package com.github.hmiyado.route

import io.ktor.application.Application
import io.ktor.routing.routing

fun Application.routing() {
    routing {
        helloWorld()
        articles()
    }
}
