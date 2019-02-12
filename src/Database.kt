package com.github.hmiyado

import io.ktor.application.Application
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

@Suppress("unused") // Referenced in application.conf
fun Application.database() {
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
}
