package com.github.hmiyado.kottage.application.configuration

import io.ktor.auth.UserPasswordCredential
import io.ktor.config.ApplicationConfig
import org.koin.core.module.Module
import org.koin.dsl.module

fun provideApplicationConfigurationModule(config: ApplicationConfig): Module = module {
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
