package com.github.hmiyado.kottage.route

import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options

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
