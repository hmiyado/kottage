package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.openapi.models.Comments
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.route.users.UsersLocation
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.options
import com.github.hmiyado.kottage.openapi.models.Comment as OpenApiComment

class EntriesSerialNumberCommentsLocation(
    private val entriesCommentsService: EntriesCommentsService,
) {
    fun addRoute(route: Route) = with(route) {
        with(OpenApi) {
            entriesSerialNumberCommentsGet {
                val serialNumber = call.entriesSerialNumberCommentsGetSerialNumber()
                val limit = call.entriesSerialNumberCommentsGetLimit()
                val offset = call.entriesSerialNumberCommentsGetOffset()
                val page = entriesCommentsService.getComments(serialNumber, limit, offset)
                call.respond(page.toOpenApiComments())
            }

            entriesSerialNumberCommentsPost { (body), user ->
                val serialNumber = call.entriesSerialNumberCommentsGetSerialNumber()
                val comment = entriesCommentsService.addComment(serialNumber, body, user)
                call.respond(HttpStatusCode.Created, comment.toOpenApiComment())
            }
        }

        options(Paths.entriesSerialNumberCommentsGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
        }
    }
}

fun Comment.toOpenApiComment(): OpenApiComment = OpenApiComment(
    id = id,
    body = body,
    createdAt = createdAt,
    author = with(UsersLocation) { author.toResponseUser() },
)

fun Page<Comment>.toOpenApiComments() = Comments(
    totalCount = totalCount,
    items = items.map { it.toOpenApiComment() }
)