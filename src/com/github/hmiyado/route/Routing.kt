package com.github.hmiyado.route

import com.github.hmiyado.route.articles.articlesSerialNumber
import io.ktor.application.Application
import io.ktor.routing.routing
import org.koin.ktor.ext.get

fun Application.routing() {
    routing {
        helloWorld()
        articles(get())
        articlesSerialNumber(get())
    }
}
