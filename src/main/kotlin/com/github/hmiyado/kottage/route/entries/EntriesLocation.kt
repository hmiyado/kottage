package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.pathComponents
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.util.url

class EntriesLocation {
    @KtorExperimentalLocationsAPI
    companion object {
        private const val path = Path.Entries
        fun addRoute(route: Route, entriesService: EntriesService) = with(route) {
            get(path) {
                call.respond(entriesService.getEntries())
            }
            with(OpenApi) {
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
