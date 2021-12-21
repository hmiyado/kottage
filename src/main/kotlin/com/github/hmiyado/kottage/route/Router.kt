package com.github.hmiyado.kottage.route

import io.ktor.routing.Route

interface Router {
    fun addRoute(route: Route)
}
