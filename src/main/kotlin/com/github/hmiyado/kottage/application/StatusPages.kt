package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.RequestBodyUnrecognizableException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun Application.statusPages() {
    install(StatusPages) {
        exception<OpenApi.RequestBodyUnrecognizableException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "request body should be json")
        }

        exception<RequestBodyUnrecognizableException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "request body should be json")
        }
    }
}
