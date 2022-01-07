package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

object OpenApiStatusPageRouter : StatusPageRouter {
    override fun addStatusPage(configuration: StatusPages.Configuration) {
        with(configuration) {
            exception<OpenApi.PathParameterUnrecognizableException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "path parameter is not valid")
            }

            exception<OpenApi.RequestBodyUnrecognizableException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "request body should be json")
            }
        }
    }
}
