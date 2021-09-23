package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.route.receiveOrThrow
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.post
import io.ktor.util.url

class EntriesLocation {
    companion object {
        private const val path = "entries"
        fun addRoute(route: Route, entriesService: EntriesService) = with(route) {
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
                    val (title, body) = call.receiveOrThrow<EntriesRequestPayload.Post>()
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
    }
}
