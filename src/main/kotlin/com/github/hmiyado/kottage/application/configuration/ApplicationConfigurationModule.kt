package com.github.hmiyado.kottage.application.configuration

import com.github.hmiyado.kottage.model.Health
import io.ktor.auth.UserPasswordCredential
import io.ktor.config.ApplicationConfig
import org.koin.core.module.Module
import org.koin.dsl.module

fun provideApplicationConfigurationModule(config: ApplicationConfig): Module = module {
    single {
        DatabaseConfiguration.detectConfiguration(
            config.config("ktor.database")
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
    single {
        Health.Version(config.propertyOrNull("ktor.application.version")?.getString() ?: "")
    }
}
