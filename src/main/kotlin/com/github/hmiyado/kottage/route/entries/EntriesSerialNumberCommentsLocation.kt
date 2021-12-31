package com.github.hmiyado.kottage.route.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.openapi.models.Comments
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.route.users.UsersLocation
import com.github.hmiyado.kottage.route.users.findUser
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.options
import com.github.hmiyado.kottage.openapi.models.Comment as OpenApiComment

class EntriesSerialNumberCommentsLocation(
    private val usersService: UsersService,
    private val entriesCommentsService: EntriesCommentsService,
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            entriesSerialNumberCommentsGet {
                val serialNumber = call.entriesSerialNumberCommentsGetSerialNumber()
                val limit = call.entriesSerialNumberCommentsGetLimit()
                val offset = call.entriesSerialNumberCommentsGetOffset()
                val page = entriesCommentsService.getComments(serialNumber, limit, offset)
                call.respond(page.toOpenApiComments())
            }

            entriesSerialNumberCommentsPost { (name, body) ->
                val user = call.findUser(usersService)
                val serialNumber = call.entriesSerialNumberCommentsGetSerialNumber()
                val comment = entriesCommentsService.addComment(serialNumber, name, body, user)
                call.respond(HttpStatusCode.Created, comment.toOpenApiComment())
            }
        }

        route.options(Paths.entriesSerialNumberCommentsGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
        }
    }
}

fun Comment.toOpenApiComment(): OpenApiComment = OpenApiComment(
    id = id,
    entryId = entryId,
    name = name,
    body = body,
    createdAt = createdAt,
    author = with(UsersLocation) { author?.toResponseUser() },
)

fun Page<Comment>.toOpenApiComments() = Comments(
    totalCount = totalCount,
    items = items.map { it.toOpenApiComment() }
)
