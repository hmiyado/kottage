package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object Entries : LongIdTable() {
    val title = varchar("title", 100)
    val body = text("body")
    val dateTime = datetime("dateTime")
    val author = reference("author", Users, onDelete = ReferenceOption.NO_ACTION, onUpdate = ReferenceOption.NO_ACTION)
}
