package com.github.hmiyado

import com.github.hmiyado.route.articles
import com.github.hmiyado.route.helloWorld
import io.ktor.application.Application
import io.ktor.routing.routing

fun Application.routing() {
    routing {
        helloWorld()
        articles()
    }
}
