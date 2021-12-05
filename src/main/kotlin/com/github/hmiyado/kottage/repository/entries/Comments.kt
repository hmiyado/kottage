package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Comments : Table() {
    val idByEntry = long("idByEntry")
    val body = text("body")
    val createdAt = datetime("createdAt")
    val entry = reference("entry", Entries, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.NO_ACTION)
    val author = reference("author", Users, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.NO_ACTION)
}
