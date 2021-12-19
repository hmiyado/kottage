package com.github.hmiyado.kottage.route

import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.content.files
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options

class RootLocation {
    companion object {
        private const val path = Path.Root
        fun addRoute(route: Route) = with(route) {
            get(path) {
                call.respondText("Hello World!")
            }

            options(path) {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
            }

            static(path) {
                resources("public")
            }
        }
    }
}
