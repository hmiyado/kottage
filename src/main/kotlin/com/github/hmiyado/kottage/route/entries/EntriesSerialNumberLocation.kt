package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.route.receiveOrThrow
import com.github.hmiyado.kottage.service.entries.EntriesService
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.delete
import io.ktor.locations.get
import io.ktor.locations.options
import io.ktor.locations.patch
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
@Location(Path.EntriesSerialNumber)
data class EntriesSerialNumberLocation(val serialNumber: Long) {
    companion object {
        fun addRoute(route: Route, entriesService: EntriesService) = with(route) {
            get<EntriesSerialNumberLocation> { (serialNumber) ->
                val entry = entriesService.getEntry(serialNumber)
                if (entry == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(entry)
            }

            authenticate("user") {
                patch<EntriesSerialNumberLocation> { (serialNumber) ->
                    val principal = call.authentication.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toLongOrNull()
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@patch
                    }
                    val (title, body) = call.receiveOrThrow<EntriesSerialNumberRequestPayload.Patch>()
                    kotlin.runCatching { entriesService.updateEntry(serialNumber, userId, title, body) }
                        .onSuccess { entry ->
                            call.respond(entry)
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

                delete<EntriesSerialNumberLocation> { (serialNumber) ->
                    val principal = call.authentication.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toLongOrNull()
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@delete
                    }

                    kotlin.runCatching { entriesService.deleteEntry(serialNumber, userId) }
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

            options<EntriesSerialNumberLocation> {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
            }
        }
    }
}
