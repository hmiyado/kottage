package com.github.hmiyado

import com.github.hmiyado.route.graphql
import com.github.hmiyado.route.helloWorld
import io.ktor.application.Application
import io.ktor.routing.routing

fun Application.routing() {
    routing {
        helloWorld()
        graphql()
    }
}
