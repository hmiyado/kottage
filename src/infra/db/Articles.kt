package com.github.hmiyado.infra.db

import org.jetbrains.exposed.sql.Table

object Articles: Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 100)
    val body = text("body")
    val dateTime = datetime("dateTime")
}