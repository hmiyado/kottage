package com.github.hmiyado.route

import com.github.hmiyado.service.articles.ArticlesService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun Route.articles(articlesService: ArticlesService) {
    get("articles") {
        call.respond(Json.encodeToString(articlesService.getArticles()))
    }
    post("articles") {
        try {
            val bodyJson = Json.parseToJsonElement(call.receiveText()).jsonObject
            val article = articlesService.createArticle(
                bodyJson["title"]!!.jsonPrimitive.content,
                bodyJson["body"]!!.jsonPrimitive.content
            )
            call.respond(Json.encodeToString(article))
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
