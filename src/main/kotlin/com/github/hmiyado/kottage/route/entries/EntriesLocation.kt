package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.model.toEntryResponse
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.options
import io.ktor.server.util.url
import com.github.hmiyado.kottage.openapi.models.Entries as EntriesResponse

class EntriesLocation(
    private val entriesService: EntriesService,
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            entriesGet {
                val limit = call.entriesGetLimit()
                val offset = call.entriesGetOffset()
                val entriesPage = entriesService.getEntries(limit, offset)
                call.respond(
                    EntriesResponse(
                        items = entriesPage.items.map { it.toEntryResponse() },
                        totalCount = entriesPage.totalCount,
                    ),
                )
            }

            entriesPost { (title, body), user ->
                val entry = entriesService.createEntry(title, body, user.id)
                call.response.header(
                    "Location",
                    this.context.url { this.appendPathSegments("${entry.serialNumber}") },
                )
                call.response.header("ContentType", ContentType.Application.Json.toString())
                call.respond(HttpStatusCode.Created, entry.toEntryResponse())
            }
        }

        route.options(Paths.entriesGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
        }
    }
}
