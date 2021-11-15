package com.github.hmiyado.kottage.repository

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import com.github.hmiyado.kottage.cli.Migration
import org.slf4j.LoggerFactory

fun initializeDatabase(databaseConfiguration: DatabaseConfiguration) {
    val logger = LoggerFactory.getLogger("Application")

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
