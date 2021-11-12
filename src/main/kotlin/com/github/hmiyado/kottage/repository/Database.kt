package com.github.hmiyado.kottage.repository

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import com.github.hmiyado.kottage.repository.entries.Entries
import com.github.hmiyado.kottage.repository.users.Passwords
import com.github.hmiyado.kottage.repository.users.Users
import com.github.hmiyado.kottage.repository.users.admins.Admins
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun initializeDatabase(databaseConfiguration: DatabaseConfiguration) {
    val logger = LoggerFactory.getLogger("Application")

    when (databaseConfiguration) {
        DatabaseConfiguration.Memory -> {
            logger.debug("database is successfully connected to memory")
        }
        is DatabaseConfiguration.Postgres -> {
            val url = "jdbc:postgresql://${databaseConfiguration.host}:5432/${databaseConfiguration.name}"
            Database.connect(
                url = url,
                driver = "org.postgresql.Driver",
                user = databaseConfiguration.user,
                password = databaseConfiguration.password
            )

            logger.debug("database is successfully connected to postgres")

            transaction {
                with(SchemaUtils) {
                    withDataBaseLock {
                        initializeDatabase()
                    }
                }
            }
        }
        is DatabaseConfiguration.MySql -> {
            val url = "jdbc:mysql://${databaseConfiguration.host}:3306/${databaseConfiguration.name}"
            Database.connect(
                url = url,
                driver = "com.mysql.jdbc.Driver",
                user = databaseConfiguration.user,
                password = databaseConfiguration.password
            )

            var retryCount = 0
            var successToConnect = false
            while (!successToConnect) {
                try {
                    transaction {
                        with(SchemaUtils) {
                            withDataBaseLock {
                                initializeDatabase()
                            }
                        }
                    }
                    successToConnect = true
                } catch (e: Throwable) {
                    retryCount += 1
                    if (retryCount > 10) {
                        throw e
                    }
                    logger.error("cannot connect to mysql after $retryCount times retry")
                    Thread.sleep(1000L * retryCount)
                }
            }

            logger.debug("database is successfully connected to mysql")
        }
    }
}

private fun initializeDatabase() {
    SchemaUtils.createMissingTablesAndColumns(Entries, Users, Passwords, Admins)
}
