package com.github.hmiyado.kottage.application.plugins

import com.github.hmiyado.kottage.application.configuration.provideApplicationConfigurationModule
import com.github.hmiyado.kottage.application.plugins.authentication.authenticationModule
import com.github.hmiyado.kottage.application.plugins.hook.httpClientModule
import com.github.hmiyado.kottage.repository.repositoryModule
import com.github.hmiyado.kottage.route.routeModule
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
        authenticationModule,
        routeModule,
        httpClientModule,
    )
    return this
}

class KoinLogger(level: Level = Level.ERROR) : Logger(level) {
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
