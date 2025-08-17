package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Admins : Table() {
    val user = Admins.reference(
        "user",
        Users,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION
    )
}
