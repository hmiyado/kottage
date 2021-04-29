package com.github.hmiyado.route

import com.github.hmiyado.repository.ArticleRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.koin.ktor.ext.inject

fun Route.articles() {
    val articleRepository: ArticleRepository by inject()
    get("articles") {
        call.respond(articleRepository.getArticles())
    }
}
