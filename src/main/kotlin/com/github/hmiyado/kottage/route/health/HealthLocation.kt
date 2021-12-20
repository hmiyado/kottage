package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.health.HealthService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options

class HealthLocation(
    private val healthService: HealthService
) {
    fun addRoute(route: Route) = with(route) {
        get(Path.Health) {
            call.respond(healthService.getHealth())
        }
        options(Path.Health) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
        }
    }
}
