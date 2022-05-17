package com.github.hmiyado.kottage.route

import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.options

class RootLocation : Router {
    override fun addRoute(route: Route) {
        route.get(path) {
            call.respondText("Hello World!")
        }

        route.options(path) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
        }

        route.static(path) {
            resources("public")
        }
    }

    companion object {
        const val path = "/"
    }
}
