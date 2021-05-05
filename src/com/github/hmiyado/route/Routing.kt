package com.github.hmiyado.route

import com.github.hmiyado.route.articles.entriesSerialNumber
import io.ktor.application.Application
import io.ktor.routing.routing
import org.koin.ktor.ext.get

fun Application.routing() {
    routing {
        helloWorld()
        entries(get())
        entriesSerialNumber(get())
    }
}
