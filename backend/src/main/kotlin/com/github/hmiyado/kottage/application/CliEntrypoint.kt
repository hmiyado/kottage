package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.repository.Migration
import io.ktor.server.engine.CommandLineConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object CliEntrypoint : KoinComponent {
    @JvmStatic
    fun main(args: Array<String>) {
        val commandLineConfig = CommandLineConfig(
            arrayOf("-config=src/main/resources/application.conf")
        )
        startKoin {
//            initializeKoinModules(commandLineConfig)
        }
        when (args.firstOrNull()) {
            "database" -> when (args.getOrNull(1)) {
                "info" -> Migration(get()).info()
                "baseline" -> Migration(get()).baseline()
                "migrate" -> Migration(get()).migrate()
                "statement" -> Migration(get()).statement()
                else -> error("Unexpected argument: ${args.joinToString()}")
            }
            else -> error("Unexpected argument: ${args.joinToString()}")
        }
        stopKoin()
    }
}
