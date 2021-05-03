package com.github.hmiyado.application

import io.ktor.application.Application
import org.koin.core.module.Module
import org.koin.dsl.module

fun provideApplicationConfigurationModule(application: Application): Module = module {
    single {
        val config = application.environment.config
        DatabaseConfiguration.detectConfiguration(
            config.propertyOrNull("ktor.database.postgres.name")?.getString(),
            config.propertyOrNull("ktor.database.postgres.host")?.getString(),
            config.propertyOrNull("ktor.database.postgres.user")?.getString(),
            config.propertyOrNull("ktor.database.postgres.password")?.getString()
        )
    }
}
