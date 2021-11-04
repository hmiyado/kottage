package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.route.users.UsersLocation
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.pathComponents
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.options
import io.ktor.util.url
import com.github.hmiyado.kottage.openapi.models.Entries as EntriesResponse
import com.github.hmiyado.kottage.openapi.models.Entry as EntryResponse

class EntriesLocation {
    companion object {
        private const val path = Path.Entries

        fun Entry.toEntryResponse() = EntryResponse(
            serialNumber = serialNumber,
            title = title,
            body = body,
            dateTime = dateTime,
            author = with(UsersLocation) { author.toResponseUser() }
        )

        fun addRoute(route: Route, entriesService: EntriesService) = with(route) {
            with(OpenApi) {
                entriesGet {
                    val entries = entriesService.getEntries()
                    call.respond(EntriesResponse(items = entries.map { it.toEntryResponse() }))
                }

                entriesPost { (title, body), userId ->
                    val entry = entriesService.createEntry(title, body, userId)
                    call.response.header(
                        "Location",
                        this.context.url { this.pathComponents("/${entry.serialNumber}") })
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
