package com.github.hmiyado

import com.github.hmiyado.module.repositoryModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.main() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    initializeDatabase()
    startKoin {
        logger(PrintLogger())
        modules(
            repositoryModule
        )
    }
    routing()
}
