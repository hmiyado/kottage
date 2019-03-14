package com.github.hmiyado

import com.github.hmiyado.module.graphqlModule
import com.github.hmiyado.module.repositoryModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import org.koin.ktor.ext.installKoin

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.main() {
    install(CallLogging)
    install(ContentNegotiation) {
        gson { }
    }
    database()
    routing()
    installKoin {
        logger()
        modules(
            repositoryModule,
            graphqlModule
        )
    }
}

