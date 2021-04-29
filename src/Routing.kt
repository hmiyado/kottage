package com.github.hmiyado

import com.github.hmiyado.route.helloWorld
import io.ktor.application.*
import io.ktor.routing.*

fun Application.routing() {
    routing {
        helloWorld()
    }
}
