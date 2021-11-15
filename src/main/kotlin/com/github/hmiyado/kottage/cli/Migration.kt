package com.github.hmiyado.kottage.cli

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import com.github.hmiyado.kottage.repository.entries.Entries
import com.github.hmiyado.kottage.repository.users.Passwords
import com.github.hmiyado.kottage.repository.users.Users
import com.github.hmiyado.kottage.repository.users.admins.Admins
import org.flywaydb.core.Flyway
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
    private val tables = arrayOf(Entries, Users, Passwords, Admins)
    private val flyway: Flyway

    init {
        flyway = initialize(databaseConfiguration)
    }

    private fun initialize(databaseConfiguration: DatabaseConfiguration): Flyway {
        if (databaseConfiguration !is DatabaseConfiguration.MySql) {
            throw IllegalArgumentException("database configuration is not my sql, $databaseConfiguration")
        }
        val url = "jdbc:mysql://${databaseConfiguration.host}:3306/${databaseConfiguration.name}"
        val flyway = Flyway
            .configure()
            .dataSource(url, databaseConfiguration.user, databaseConfiguration.password)
            .load()
        Database.connect(
            url = url,
            driver = "com.mysql.jdbc.Driver",
            user = databaseConfiguration.user,
            password = databaseConfiguration.password
        )
        return flyway
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
        val result = flyway.migrate()
        transaction {
            with(SchemaUtils) {
                checkMappingConsistence(*tables)
            }
        }
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
}
