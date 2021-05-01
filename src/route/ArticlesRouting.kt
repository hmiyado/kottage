package com.github.hmiyado.route

import com.github.hmiyado.service.articles.ArticlesService
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Route.articles() {
    val articlesService: ArticlesService by inject()
    get("articles") {
        call.respond(Json.encodeToString(articlesService.getArticles()))
    }
}
