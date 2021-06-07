package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.post
import io.ktor.util.url

fun Route.entries(entriesService: EntriesService) {
    val path = "entries"
    get(path) {
        call.respond(entriesService.getEntries())
    }
    authenticate("user") {
        post(path) {
            val principal = call.authentication.principal<UserIdPrincipal>()
            val userId = principal?.name?.toLongOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val requestBody = kotlin.runCatching { call.receiveOrNull<Map<String, String>>() }.getOrNull()
            val (title, body) = requestBody?.get("title") to requestBody?.get("body")
            if (title == null || body == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val entry = entriesService.createEntry(title, body, userId)
            call.response.header("Location", this.context.url { this.path("entries/${entry.serialNumber}") })
            call.response.header("ContentType", ContentType.Application.Json.toString())
            call.respond(HttpStatusCode.Created, entry)
        }
    }

    options(path) {
        call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
    }
}
