package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.patch

fun Route.entriesSerialNumber(entriesService: EntriesService) {
    val path = "/entries/{serialNumber}"
    get(path) {
        val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
        if (serialNumber == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val entry = entriesService.getEntry(serialNumber)
        if (entry == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(entry)
    }

    authenticate("user") {
        patch(path) {
            val principal = call.authentication.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@patch
            }
            val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
            if (serialNumber == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }
            val bodyJson = kotlin.runCatching { call.receiveOrNull<Map<String, String>>() }.getOrNull() ?: emptyMap()
            val entry = entriesService.updateEntry(serialNumber, bodyJson["title"], bodyJson["body"])
            if (entry == null) {
                call.respond(HttpStatusCode.NotFound)
                return@patch
            }
            call.respond(entry)
        }

        delete(path) {
            val principal = call.authentication.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
            if (serialNumber == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            entriesService.deleteEntry(serialNumber)
            call.respond(HttpStatusCode.OK)
        }
    }

    options(path) {
        call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
    }
}
