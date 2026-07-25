package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.provideApplicationConfigurationModule
import com.github.hmiyado.kottage.repository.Migration
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

object CliEntrypoint : KoinComponent {
    private val logger = LoggerFactory.getLogger("cli")

    @JvmStatic
    fun main(args: Array<String>) {
        // ConfigFactory.load() resolves application.conf from the classpath the same way
        // Ktor's EngineMain does for the server, including ${?ENV_VAR} substitutions. This
        // works both from `./gradlew run` (classes on classpath) and from the packaged
        // distribution/jar, unlike a hardcoded file path relative to the working directory.
        val config = HoconApplicationConfig(ConfigFactory.load())
        startKoin {
            modules(provideApplicationConfigurationModule(config))
        }
        when (args.firstOrNull()) {
            // Standalone entrypoint for running Flyway migrations without booting the
            // Ktor application (Lambda migration phase3: decouple migration from app startup).
            // Intended to be run as a one-off step before deployment. Exit code reflects
            // success/failure so it can gate a CI pipeline.
            "migrate" -> migrate()
            "database" ->
                when (args.getOrNull(1)) {
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

    private fun migrate() {
        runCatching { Migration(get()).migrate() }
            .onFailure { e ->
                logger.error("migration failed: {}", e.message, e)
                exitProcess(1)
            }
        exitProcess(0)
    }
}
