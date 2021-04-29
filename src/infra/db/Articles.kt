package com.github.hmiyado.infra.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Articles : IntIdTable() {
    val title = varchar("title", 100)
    val body = text("body")
    val dateTime = datetime("dateTime")
}