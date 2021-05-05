package com.github.hmiyado.kottage.repository.entries

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Entries : LongIdTable() {
    val title = varchar("title", 100)
    val body = text("body")
    val dateTime = datetime("dateTime")
}
