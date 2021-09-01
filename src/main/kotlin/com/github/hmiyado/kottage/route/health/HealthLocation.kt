package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.health.HealthService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options

class HealthLocation {
    companion object {
        fun addRoute(route: Route, healthService: HealthService) = with(route) {
            get("/health") {
                call.respond(healthService.getHealth())
            }
            options("/health") {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
            }
        }
    }
}
