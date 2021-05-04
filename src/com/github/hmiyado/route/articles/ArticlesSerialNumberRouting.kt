package com.github.hmiyado.route.articles

import com.github.hmiyado.service.articles.ArticlesService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get

fun Route.articlesSerialNumber(articlesService: ArticlesService) {
    get("/articles/{serialNumber}") {
        val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
        if (serialNumber == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val article = articlesService.getArticle(serialNumber)
        if (article == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(article)
    }

    authenticate {
        delete("/articles/{serialNumber}") {
            val serialNumber = call.parameters["serialNumber"]?.toLongOrNull()
            if (serialNumber == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            articlesService.deleteArticle(serialNumber)
            call.respond(HttpStatusCode.OK)
        }
    }
}
