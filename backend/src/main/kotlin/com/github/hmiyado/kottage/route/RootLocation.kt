package com.github.hmiyado.kottage.route

import io.ktor.http.HttpMethod
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.options

class RootLocation : Router {
    override fun addRoute(route: Route) {
        route.get(PATH) {
            call.respondText("Hello World!")
        }

        route.options(PATH) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
        }

        route.staticResources(PATH, basePackage = "public")
    }

    companion object {
        const val PATH = "/"
    }
}
