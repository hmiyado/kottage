package com.github.hmiyado.kottage.cli

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import com.github.hmiyado.kottage.repository.entries.Comments
import com.github.hmiyado.kottage.repository.entries.Entries
import com.github.hmiyado.kottage.repository.users.Passwords
import com.github.hmiyado.kottage.repository.users.Users
import com.github.hmiyado.kottage.repository.users.admins.Admins
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.exception.FlywayValidateException
import org.flywaydb.core.api.output.MigrateResult
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Migration(
    databaseConfiguration: DatabaseConfiguration,
    private val logger: Logger = LoggerFactory.getLogger("cli")
) {
    private val flyway: Flyway

    init {
        flyway = initialize(databaseConfiguration)
    }

    private fun initialize(databaseConfiguration: DatabaseConfiguration): Flyway {
        if (databaseConfiguration !is DatabaseConfiguration.MySql) {
            throw IllegalArgumentException("database configuration is not my sql, $databaseConfiguration")
        }
        return databaseConfiguration.init()
    }

    fun info() {
        val info = flyway.info()
        val current = info.current()
        listOf(
            current.description,
            current.state,
            current.type,
            current.version,
            current.script,
        ).forEach {
            logger.info("{}", it)
        }
    }

    fun baseline() {
        flyway.baseline()
        flyway.info()
    }

    fun migrate() {
        val result = kotlin.runCatching { flyway.migrate() }
            .onFailure { e ->
                when (e) {
                    is FlywayValidateException -> {
                        logger.error("FlywayValidateException:{}", e.message)
                        logger.error("error code:{}", e.errorCode)
                    }
                    else -> {
                        logger.error(e.message)
                    }
                }
            }
            .getOrThrow()
        checkExposedTableMapping()
        logger.info(result.formatLog())
    }

    fun statement() {
        val statements = transaction {
            with(SchemaUtils) {
                createStatements(*tables)
            }
        }
        logger.info("@start_statement\n{}\n@end_statement", statements.joinToString(separator = "\n"))
    }

    private fun MigrateResult.formatLog(): String {
        val isSuccess = if (success) {
            "SUCCESS"
        } else {
            "FAILURE"
        }

        return """
        [$isSuccess]: $initialSchemaVersion -> $targetSchemaVersion
        """.trimIndent()
    }

    companion object {
        private val tables = arrayOf(Entries, Users, Passwords, Admins, Comments)

        fun checkExposedTableMapping() {
            transaction {
                with(SchemaUtils) {
                    checkMappingConsistence(*tables)
                }
            }
        }

        private fun DatabaseConfiguration.MySql.init(): Flyway {
            val url = "jdbc:mysql://${host}:3306/${name}"
            val flyway = Flyway
                .configure()
                .baselineOnMigrate(true)
                .dataSource(url, user, password)
                .load()
            Database.connect(
                url = url,
                driver = "com.mysql.jdbc.Driver",
                user = user,
                password = password
            )
            return flyway
        }
    }
}
