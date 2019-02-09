package com.github.hmiyado

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.routing(testing: Boolean = false) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
