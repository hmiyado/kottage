package com.github.hmiyado.kottage.application.configuration

import com.github.hmiyado.application.build.BuildConfig
import com.github.hmiyado.kottage.model.Health
import io.ktor.http.HttpMethod
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.config.ApplicationConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
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
            config.config("ktor.database"),
        )
    }
    single {
        RedisConfiguration(config.property("ktor.redis.host").getString())
    }
    single {
        val redisConfiguration: RedisConfiguration = get()
        JedisPool(JedisPoolConfig(), redisConfiguration.host)
    }
    single {
        AuthenticationConfiguration(
            adminCredential = UserPasswordCredential(
                config.property("ktor.authentication.admin.name").getString(),
                config.property("ktor.authentication.admin.password").getString(),
            ),
        )
    }
    single {
        OauthGoogle(
            clientId = config.property("ktor.authentication.oauth.google.clientId").getString(),
            clientSecret = config.property("ktor.authentication.oauth.google.clientSecret").getString(),
            callbackUrl = config.property("ktor.authentication.oauth.google.callbackUrl").getString(),
            defaultRedirectUrl = config.property("ktor.authentication.oauth.google.defaultRedirectUrl").getString(),
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
    single(named("HookConfigurations")) {
        config
            .configList("ktor.hooks")
            .map { hookConfig ->
                HookConfiguration(
                    name = hookConfig.property("name").getString(),
                    method = hookConfig
                        .property("method")
                        .getString().uppercase()
                        .let {
                            HttpMethod(it)
                        },
                    path = hookConfig.property("path").getString(),
                    requestTo = hookConfig.property("requestTo").getString(),
                )
            }
    }
}
