package com.github.hmiyado.application.configuration

import io.ktor.application.Application
import io.ktor.auth.UserPasswordCredential
import org.koin.core.module.Module
import org.koin.dsl.module

fun provideApplicationConfigurationModule(application: Application): Module = module {
    val config = application.environment.config
    single {
        DatabaseConfiguration.detectConfiguration(
            config.propertyOrNull("ktor.database.postgres.name")?.getString(),
            config.propertyOrNull("ktor.database.postgres.host")?.getString(),
            config.propertyOrNull("ktor.database.postgres.user")?.getString(),
            config.propertyOrNull("ktor.database.postgres.password")?.getString()
        )
    }
    single {
        AuthenticationConfiguration(
            adminCredential = UserPasswordCredential(
                config.property("ktor.authentication.admin.name").getString(),
                config.property("ktor.authentication.admin.password").getString()
            )
        )
    }
}
