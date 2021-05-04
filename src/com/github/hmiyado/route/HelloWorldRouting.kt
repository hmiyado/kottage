package com.github.hmiyado.route

import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options

fun Route.helloWorld() {
    get("/") {
        call.respondText("Hello World!")
    }

    options("/") {
        call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
    }
}
