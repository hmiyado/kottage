package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

object OpenApiStatusPageRouter : StatusPageRouter {
    override fun addStatusPage(configuration: StatusPagesConfig) {
        with(configuration) {
            exception<OpenApi.PathParameterUnrecognizableException> { call, _ ->
                call.respond(HttpStatusCode.BadRequest, ErrorFactory.create400("path parameter is not valid"))
            }

            exception<OpenApi.RequestBodyUnrecognizableException> { call, _ ->
                call.respond(HttpStatusCode.BadRequest, ErrorFactory.create400("request body is not valid"))
            }
        }
    }
}
