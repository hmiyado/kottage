package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.route.RequestBodyUnrecognizableException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun Application.statusPages() {
    install(StatusPages) {
        exception<RequestBodyUnrecognizableException> {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
