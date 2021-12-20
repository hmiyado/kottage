package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.model.toEntryResponse
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.options

class EntriesSerialNumberLocation(
    private val entriesService: EntriesService
) {
    fun addRoute(route: Route) = with(route) {
        with(OpenApi) {
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
                kotlin.runCatching { entriesService.updateEntry(serialNumber, user.id, title, body) }
                    .onSuccess { entry ->
                        call.respond(entry.toEntryResponse())
                    }
                    .onFailure { throwable ->
                        when (throwable) {
                            is EntriesService.NoSuchEntryException -> {
                                call.respond(HttpStatusCode.NotFound)
                            }
                            is EntriesService.ForbiddenOperationException -> {
                                call.respond(HttpStatusCode.Forbidden)
                            }
                            else -> {
                                call.respond(HttpStatusCode.BadRequest)
                            }
                        }
                    }
            }
            entriesSerialNumberDelete { user ->
                val serialNumber = call.entriesSerialNumberDeleteSerialNumber()
                kotlin.runCatching { entriesService.deleteEntry(serialNumber, user.id) }
                    .onSuccess {
                        call.respond(HttpStatusCode.OK)
                    }
                    .onFailure { throwable ->
                        when (throwable) {
                            is EntriesService.ForbiddenOperationException -> call.respond(HttpStatusCode.Forbidden)
                            else -> call.respond(HttpStatusCode.BadRequest)
                        }
                    }
            }
        }

        options(Paths.entriesSerialNumberGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
        }
    }

    companion object {
        fun addStatusPage(configuration: StatusPages.Configuration) = with(configuration) {
            exception<EntriesService.NoSuchEntryException> { cause ->
                call.respond(HttpStatusCode.NotFound, cause.message ?: "No such entry")
            }
        }
    }
}
