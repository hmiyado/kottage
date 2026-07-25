package com.github.hmiyado.kottage.repository

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import org.jetbrains.exposed.v1.jdbc.Database
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun initializeDatabase(databaseConfiguration: DatabaseConfiguration) {
    when (databaseConfiguration) {
        DatabaseConfiguration.Memory -> {
            logger.debug("database is successfully connected to memory")
        }
        is DatabaseConfiguration.Postgres -> {
//            val url = "jdbc:postgresql://${databaseConfiguration.host}:5432/${databaseConfiguration.name}"
//            Database.connect(
//                url = url,
//                driver = "org.postgresql.Driver",
//                user = databaseConfiguration.user,
//                password = databaseConfiguration.password
//            )
//
//            logger.debug("database is successfully connected to postgres")
            throw IllegalStateException("todo postgres connection")
        }
        is DatabaseConfiguration.MySql -> {
            val migration = Migration(databaseConfiguration)
            tryConnect@ for (retryCount in 1..10) {
                try {
                    migration.migrate()
                    break@tryConnect
                } catch (e: Throwable) {
                    if (retryCount == 10) {
                        throw e
                    }
                    logger.error("cannot connect to mysql after $retryCount times trial")
                    Thread.sleep(1000L * retryCount)
                }
            }

            logger.debug("database is successfully connected to mysql")
        }
    }
}

/**
 * Establishes the Exposed DB connection without running Flyway migrations.
 *
 * `initializeDatabase` above establishes the connection as a side effect of building the
 * `Migration`/Flyway instance. When startup migrations are disabled (`RUN_MIGRATION_ON_STARTUP`,
 * see Lambda migration phase3), the app still needs a working Exposed connection to serve
 * requests even though migrations are run separately (see CliEntrypoint's `migrate` subcommand).
 */
fun connectDatabaseWithoutMigration(databaseConfiguration: DatabaseConfiguration) {
    when (databaseConfiguration) {
        DatabaseConfiguration.Memory -> {
            logger.debug("database is successfully connected to memory")
        }
        is DatabaseConfiguration.Postgres -> {
            throw IllegalStateException("todo postgres connection")
        }
        is DatabaseConfiguration.MySql -> {
            databaseConfiguration.connect()
            logger.debug("database is successfully connected to mysql (startup migration skipped)")
        }
    }
}

/** Builds the JDBC URL for a MySQL/TiDB connection, applying SSL params for the given sslMode. */
fun DatabaseConfiguration.MySql.jdbcUrl(): String {
    val sslParams =
        when (sslMode.uppercase()) {
            "REQUIRED" -> "sslMode=REQUIRED&enabledTLSProtocols=TLSv1.2,TLSv1.3"
            "DISABLED" -> "useSSL=false&allowPublicKeyRetrieval=true"
            else -> "useSSL=false&allowPublicKeyRetrieval=true"
        }
    return "jdbc:mysql://$host:$port/$name?$sslParams"
}

/** Registers this configuration as Exposed's default database connection. */
fun DatabaseConfiguration.MySql.connect(): Database =
    Database.connect(
        url = jdbcUrl(),
        driver = "com.mysql.cj.jdbc.Driver",
        user = user,
        password = password,
    )
