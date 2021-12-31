package com.github.hmiyado.kottage.route.comments

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.entries.toOpenApiComments
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route

class CommentsLocation(
    private val entriesCommentsService: EntriesCommentsService
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            commentsGet {
                val limit = call.commentsGetLimit()
                val offset = call.commentsGetOffset()
                val page = entriesCommentsService.getComments(null, limit, offset)
                call.respond(page.toOpenApiComments())
            }
        }
    }
}
