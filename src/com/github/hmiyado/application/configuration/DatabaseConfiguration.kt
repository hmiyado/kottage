package com.github.hmiyado.application.configuration

sealed class DatabaseConfiguration {
    object Memory : DatabaseConfiguration()

    data class Postgres(
        val name: String,
        val host: String,
        val user: String,
        val password: String
    ) : DatabaseConfiguration()

    companion object {
        fun detectConfiguration(
            postgresName: String?,
            postgresHost: String?,
            postgresUser: String?,
            postgresPassword: String?
        ): DatabaseConfiguration {
            return if (postgresName?.isNotEmpty() == true
                && postgresHost?.isNotEmpty() == true
                && postgresUser?.isNotEmpty() == true
                && postgresPassword?.isNotEmpty() == true
            ) {
                Postgres(postgresName, postgresHost, postgresUser, postgresPassword)
            } else {
                Memory
            }
        }
    }
}
