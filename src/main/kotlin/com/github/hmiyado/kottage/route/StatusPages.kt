package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfHeaderException
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfTokenException
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsCommentIdLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberLocation
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun Application.statusPages() {
    install(StatusPages) {
        exception<OpenApi.PathParameterUnrecognizableException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "path parameter is not valid")
        }

        exception<OpenApi.RequestBodyUnrecognizableException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "request body should be json")
        }

        exception<CsrfTokenException> {
            call.respond(HttpStatusCode.Forbidden, "Csrf Token is invalid")
        }

        exception<CsrfHeaderException> {
            call.respond(HttpStatusCode.Forbidden, "Csrf Header ${CustomHeaders.XCSRFToken} is required")
        }

        EntriesSerialNumberLocation.addStatusPage(this)

        EntriesSerialNumberCommentsCommentIdLocation.addStatusPage(this)
    }
}
