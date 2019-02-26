package com.github.hmiyado

import com.github.hmiyado.infra.db.Articles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun database() {
    val logger = LoggerFactory.getLogger("Application database")

    val databaseName = System.getenv("POSTGRES_DB")
    val user = System.getenv("POSTGRES_USER")
    val password = System.getenv("POSTGRES_PASSWORD")
    val host = System.getenv("POSTGRES_HOST")
    val url = "jdbc:postgresql://$host:5432/$databaseName"
    Database.connect(
        url = url,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    logger.debug("database is successfully connected")

    transaction {
        SchemaUtils.create(Articles)
    }
}
