package com.github.hmiyado.kottage.route

import io.ktor.features.StatusPages
import io.ktor.routing.Route

interface Router {
    fun addRoute(route: Route)
}

interface StatusPageRouter {
    fun addStatusPage(configuration: StatusPages.Configuration)
}
