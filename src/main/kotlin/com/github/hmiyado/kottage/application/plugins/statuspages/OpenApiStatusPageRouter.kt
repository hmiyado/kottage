package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

object OpenApiStatusPageRouter : StatusPageRouter {
    override fun addStatusPage(configuration: StatusPagesConfig) {
        with(configuration) {
            exception<OpenApi.PathParameterUnrecognizableException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "path parameter is not valid")
            }

            exception<OpenApi.RequestBodyUnrecognizableException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "request body should be json")
            }
        }
    }
}
