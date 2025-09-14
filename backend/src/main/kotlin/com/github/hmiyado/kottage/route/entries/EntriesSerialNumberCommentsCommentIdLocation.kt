package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.StatusPageRouter
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.options

class EntriesSerialNumberCommentsCommentIdLocation(
    private val entriesCommentsService: EntriesCommentsService,
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            entriesSerialNumberCommentsCommentIdDelete { user ->
                val serialNumber = call.entriesSerialNumberCommentsCommentIdDeleteSerialNumber()
                val commentId = call.entriesSerialNumberCommentsCommentIdDeleteCommentId()
                entriesCommentsService.removeComment(serialNumber, commentId, user)
                call.respond(HttpStatusCode.OK)
            }
        }

        route.options(Paths.entriesSerialNumberCommentsCommentIdDelete) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Delete)
        }
    }

    companion object : StatusPageRouter {
        override fun addStatusPage(configuration: StatusPagesConfig) =
            with(configuration) {
                exception<EntriesCommentsService.ForbiddenOperationException> { call, _ ->
                    call.respond(HttpStatusCode.Forbidden, ErrorFactory.create403())
                }

                exception<EntriesCommentsService.NoSuchCommentException> { call, cause ->
                    call.respond(HttpStatusCode.NotFound, cause.message ?: "no such comment")
                }
            }
    }
}
