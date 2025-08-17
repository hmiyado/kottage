package com.github.hmiyado.kottage.route

import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.routing.Route

interface Router {
    fun addRoute(route: Route)
}

interface StatusPageRouter {
    fun addStatusPage(configuration: StatusPagesConfig)
}
