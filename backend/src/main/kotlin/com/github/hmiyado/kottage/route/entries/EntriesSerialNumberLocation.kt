package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.model.toEntryResponse
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.StatusPageRouter
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.options

class EntriesSerialNumberLocation(
    private val entriesService: EntriesService,
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            entriesSerialNumberGet {
                val serialNumber = call.entriesSerialNumberGetSerialNumber()
                val entry = entriesService.getEntry(serialNumber)
                if (entry == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@entriesSerialNumberGet
                }
                call.respond(entry.toEntryResponse())
            }
            entriesSerialNumberPatch { (title, body), user ->
                val serialNumber = call.entriesSerialNumberPatchSerialNumber()
                val entry = entriesService.updateEntry(serialNumber, user.id, title, body)
                call.respond(entry.toEntryResponse())
            }
            entriesSerialNumberDelete { user ->
                val serialNumber = call.entriesSerialNumberDeleteSerialNumber()
                entriesService.deleteEntry(serialNumber, user.id)
                call.respond(HttpStatusCode.OK)
            }
        }

        route.options(Paths.entriesSerialNumberGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
        }
    }

    companion object : StatusPageRouter {
        override fun addStatusPage(configuration: StatusPagesConfig) =
            with(configuration) {
                exception<EntriesService.NoSuchEntryException> { call, cause ->
                    call.respond(HttpStatusCode.NotFound, cause.message ?: "No such entry")
                }

                exception<EntriesService.ForbiddenOperationException> { call, _ ->
                    call.respond(HttpStatusCode.Forbidden, ErrorFactory.create403())
                }
            }
    }
}
