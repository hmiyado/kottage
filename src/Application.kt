package com.github.hmiyado

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import org.koin.ktor.ext.installKoin

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.main() {
    install(CallLogging)
    database()
    routing()
    installKoin {
        logger()
        modules(graphqlModule)
    }
}

