package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object Comments : LongIdTable() {
    val idByEntry = long("idByEntry")
    val name = varchar("name", 100)
    val body = text("body")
    val createdAt = datetime("createdAt")
    val entry = reference("entry", Entries, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.NO_ACTION)
    val author = optReference("author", Users, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.NO_ACTION)
}
