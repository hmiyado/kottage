package com.github.hmiyado.kottage.route

import io.ktor.application.Application
import io.ktor.routing.routing
import org.koin.ktor.ext.get

fun Application.routing() {
    routing {
        for (router in get<List<Router>>()) {
            router.addRoute(this)
        }
    }
}
