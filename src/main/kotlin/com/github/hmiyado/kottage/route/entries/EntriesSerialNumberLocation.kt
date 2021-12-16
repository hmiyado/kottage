package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.model.toEntryResponse
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.options

class EntriesSerialNumberLocation() {
    companion object {
        fun addRoute(route: Route, entriesService: EntriesService) = with(route) {
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
    }
}
