package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route

class EntriesSerialNumberCommentsCommentIdLocation(
    private val entriesCommentsService: EntriesCommentsService
) {
    fun addRoute(route: Route) = with(route) {
        with(OpenApi) {
            entriesSerialNumberCommentsCommentIdDelete { user ->
                val serialNumber = call.entriesSerialNumberCommentsCommentIdDeleteSerialNumber()
                val commentId = call.entriesSerialNumberCommentsCommentIdDeleteCommentId()
                entriesCommentsService.removeComment(serialNumber, commentId, user)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    companion object {
        fun addStatusPage(configuration: StatusPages.Configuration) = with(configuration) {
            exception<EntriesCommentsService.ForbiddenOperationException> { cause ->
                call.respond(HttpStatusCode.Forbidden, cause.message ?: "no such comment")
            }

            exception<EntriesCommentsService.NoSuchCommentException> { cause ->
                call.respond(HttpStatusCode.NotFound, cause.message ?: "no such comment")
            }
        }
    }
}
