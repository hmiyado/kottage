package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.model.toOpenApiHealth
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.health.HealthService
import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.options

class HealthLocation(
    private val healthService: HealthService
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            healthGet {
                call.respond(healthService.getHealth().toOpenApiHealth())
            }
        }
        route.options(Paths.healthGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
        }
    }
}
