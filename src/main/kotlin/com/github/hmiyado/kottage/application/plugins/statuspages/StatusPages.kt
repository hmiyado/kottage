package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

fun Application.statusPages() {
    install(StatusPages) {
        for (router in this@statusPages.get<List<StatusPageRouter>>(named("StatusPageRouter"))) {
            router.addStatusPage(this)
        }
        exception<OpenApi.RequestBodyUnrecognizableException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, buildJsonObject {
                put("status", 400)
                put("description", cause.message ?: "")
            })
        }
    }
}
