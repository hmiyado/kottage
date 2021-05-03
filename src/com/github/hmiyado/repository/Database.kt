package com.github.hmiyado.repository

import com.github.hmiyado.application.configuration.DatabaseConfiguration
import com.github.hmiyado.repository.articles.Articles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun initializeDatabase(databaseConfiguration: DatabaseConfiguration) {
    val logger = LoggerFactory.getLogger("Application database")

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
                SchemaUtils.create(Articles)
            }
        }
    }
}
