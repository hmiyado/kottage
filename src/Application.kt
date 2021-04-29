package com.github.hmiyado

import com.github.hmiyado.module.graphqlModule
import com.github.hmiyado.module.repositoryModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.main() {
    install(CallLogging)
    install(ContentNegotiation) {
        gson { }
    }
    database()
    routing()
    startKoin {
        logger(PrintLogger())
        modules(
            repositoryModule,
            graphqlModule
        )
    }
}

