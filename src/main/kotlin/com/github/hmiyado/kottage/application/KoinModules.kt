package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.provideApplicationConfigurationModule
import com.github.hmiyado.kottage.authentication.authenticationModule
import com.github.hmiyado.kottage.repository.repositoryModule
import com.github.hmiyado.kottage.service.serviceModule
import io.ktor.application.ApplicationEnvironment
import org.koin.core.KoinApplication
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.slf4j.LoggerFactory

fun KoinApplication.initializeKoinModules(environment: ApplicationEnvironment): KoinApplication {
    logger(KoinLogger())
    modules(
        provideApplicationConfigurationModule(environment.config),
        repositoryModule,
        serviceModule,
        authenticationModule
    )
    return this
}

class KoinLogger(level: Level = Level.INFO) : Logger(level) {
    private val logger = LoggerFactory.getLogger("Application")
    override fun log(level: Level, msg: MESSAGE) {
        if (this.level > level) return
        val logging: (String, Any, Any) -> Unit = when (level) {
            Level.DEBUG -> logger::debug
            Level.INFO -> logger::info
            Level.ERROR -> logger::error
            Level.NONE -> logger::trace
        }
        logging("{} {}", KOIN_TAG, msg)
    }
}
