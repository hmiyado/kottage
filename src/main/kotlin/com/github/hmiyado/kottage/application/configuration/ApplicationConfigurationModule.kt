package com.github.hmiyado.kottage.application.configuration

import com.github.hmiyado.application.build.BuildConfig
import com.github.hmiyado.kottage.model.Health
import io.ktor.auth.UserPasswordCredential
import io.ktor.config.ApplicationConfig
import org.koin.core.module.Module
import org.koin.dsl.module
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

fun provideApplicationConfigurationModule(config: ApplicationConfig): Module = module {
    single {
        val isDevelopment = config.property("ktor.development").getString().toBooleanStrictOrNull()
        if (isDevelopment == null || isDevelopment) {
            DevelopmentConfiguration.Development
        } else {
            DevelopmentConfiguration.Production
        }
    }
    single {
        DatabaseConfiguration.detectConfiguration(
            config.config("ktor.database")
        )
    }
    single {
        JedisPool(JedisPoolConfig(), "redis")
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
        Health.Version(BuildConfig.version)
    }
    single {
        val type = when (get<DatabaseConfiguration>()) {
            DatabaseConfiguration.Memory -> "memory"
            is DatabaseConfiguration.MySql -> "mysql"
            is DatabaseConfiguration.Postgres -> "postgres"
        }
        Health.DatabaseType(type)
    }
}
