package com.github.hmiyado.route

import com.github.hmiyado.service.articles.EntriesService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.post
import io.ktor.util.url

fun Route.entries(entriesService: EntriesService) {
    val path = "entries"
    get(path) {
        call.respond(entriesService.getEntries())
    }
    authenticate {
        post(path) {
            val requestBody = kotlin.runCatching { call.receiveOrNull<Map<String, String>>() }.getOrNull()
            val (title, body) = requestBody?.get("title") to requestBody?.get("body")
            if (title == null || body == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val article = entriesService.createEntry(title, body)
            call.response.header("Location", this.context.url { this.path("articles/${article.serialNumber}") })
            call.response.header("ContentType", ContentType.Application.Json.toString())
            call.respond(HttpStatusCode.Created, article)
        }
    }

    options(path) {
        call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
    }
}
