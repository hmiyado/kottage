package com.github.hmiyado.kottage.route

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get

fun Application.routing() {
    routing {
        for (router in get<List<Router>>()) {
            router.addRoute(this)
        }
    }
}
