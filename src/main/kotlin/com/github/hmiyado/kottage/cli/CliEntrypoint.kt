package com.github.hmiyado.kottage.cli

import com.github.hmiyado.kottage.application.configuration.provideApplicationConfigurationModule
import com.github.hmiyado.kottage.authentication.authenticationModule
import com.github.hmiyado.kottage.repository.repositoryModule
import com.github.hmiyado.kottage.service.serviceModule
import io.ktor.server.engine.commandLineEnvironment
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object CliEntrypoint : KoinComponent {
    @JvmStatic
    fun main(args: Array<String>) {
        val applicationEngineEnvironment =
            commandLineEnvironment(arrayOf("-config=src/main/resources/application.conf"))
        startKoin {
            modules(
                provideApplicationConfigurationModule(applicationEngineEnvironment.config),
                repositoryModule,
                serviceModule,
                authenticationModule
            )
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
