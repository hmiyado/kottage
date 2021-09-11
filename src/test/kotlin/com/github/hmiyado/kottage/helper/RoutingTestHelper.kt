package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.application.statusPages
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.locations.Locations
import io.ktor.routing.Route
import io.ktor.routing.routing

class RoutingTestHelper {
    companion object {
        fun setupRouting(
            application: Application,
            installAuthentication: (Application) -> Unit = {},
            routing: Route.() -> Unit = {}
        ) = with(application) {
            // authentication should be installed before routing
            installAuthentication(application)
            // locations should be installed before routing
            install(Locations)
            statusPages()
            contentNegotiation()
            routing {
                routing()
            }
        }
    }
}
