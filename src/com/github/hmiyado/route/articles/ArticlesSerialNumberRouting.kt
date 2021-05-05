package com.github.hmiyado.route.articles

import com.github.hmiyado.route.allowMethods
import com.github.hmiyado.service.articles.EntriesService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.patch

fun Route.articlesSerialNumber(entriesService: EntriesService) {
    get("/articles/{serialNumber}") {
        val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
        if (serialNumber == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val article = entriesService.getEntry(serialNumber)
        if (article == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(article)
    }

    authenticate {
        patch("/articles/{serialNumber}") {
            val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
            if (serialNumber == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }
            val bodyJson = kotlin.runCatching { call.receiveOrNull<Map<String, String>>() }.getOrNull() ?: emptyMap()
            val article = entriesService.updateEntry(serialNumber, bodyJson["title"], bodyJson["body"])
            if (article == null) {
                call.respond(HttpStatusCode.NotFound)
                return@patch
            }
            call.respond(article)
        }

        delete("/articles/{serialNumber}") {
            val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
            if (serialNumber == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            entriesService.deleteEntry(serialNumber)
            call.respond(HttpStatusCode.OK)
        }
    }

    options("/articles/{serialNumber}") {
        call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
    }
}
