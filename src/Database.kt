package com.github.hmiyado

import com.github.hmiyado.infra.db.Articles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

enum class Database {
    Postgres, Memory
}

fun initializeDatabase(): com.github.hmiyado.Database {
    val logger = LoggerFactory.getLogger("Application database")

    when (val user: String? = System.getenv("POSTGRES_USER")) {
        null -> {
            logger.debug("database is successfully connected to memory")
            return com.github.hmiyado.Database.Memory
        }
        else -> {
            val databaseName = System.getenv("POSTGRES_DB")
            val password = System.getenv("POSTGRES_PASSWORD")
            val host = System.getenv("POSTGRES_HOST")
            val url = "jdbc:postgresql://$host:5432/$databaseName"
            Database.connect(
                url = url,
                driver = "org.postgresql.Driver",
                user = user,
                password = password
            )

            logger.debug("database is successfully connected to postgres")

            transaction {
                SchemaUtils.create(Articles)
            }
            return com.github.hmiyado.Database.Postgres
        }
    }
}
