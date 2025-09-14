package com.github.hmiyado.kottage.application.configuration

import io.ktor.server.config.ApplicationConfig

sealed class DatabaseConfiguration {
    object Memory : DatabaseConfiguration()

    data class Postgres(
        val name: String,
        val host: String,
        val user: String,
        val password: String,
    ) : DatabaseConfiguration()

    data class MySql(
        val name: String,
        val host: String,
        val user: String,
        val password: String,
    ) : DatabaseConfiguration()

    companion object {
        fun detectConfiguration(config: ApplicationConfig?): DatabaseConfiguration {
            val postgresProperties = DatabaseProperties.from(runCatching { config?.config("postgres") }.getOrNull())
            val mySqlProperties = DatabaseProperties.from(runCatching { config?.config("mysql") }.getOrNull())
            return if (postgresProperties != null) {
                val (name, host, user, password) = postgresProperties
                Postgres(name, host, user, password)
            } else if (mySqlProperties != null) {
                val (name, host, user, password) = mySqlProperties
                MySql(name, host, user, password)
            } else {
                Memory
            }
        }
    }

    private data class DatabaseProperties(
        val name: String,
        val host: String,
        val user: String,
        val password: String,
    ) {
        companion object {
            fun from(config: ApplicationConfig?): DatabaseProperties? {
                config ?: return null
                val name = config.propertyOrNull("name")?.getString()?.getNotBlankStringOrNull() ?: return null
                val host = config.propertyOrNull("host")?.getString()?.getNotBlankStringOrNull() ?: return null
                val user = config.propertyOrNull("user")?.getString()?.getNotBlankStringOrNull() ?: return null
                val password = config.propertyOrNull("password")?.getString()?.getNotBlankStringOrNull() ?: return null
                return DatabaseProperties(name, host, user, password)
            }

            private fun String?.getNotBlankStringOrNull(): String? {
                this ?: return null
                if (this.isBlank()) {
                    return null
                }
                return this
            }
        }
    }
}
