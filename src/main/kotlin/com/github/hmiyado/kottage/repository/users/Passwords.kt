package com.github.hmiyado.kottage.repository.users

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Passwords : Table() {
    val user = Passwords.reference(
        "user",
        Users,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION
    )
    val password = Users.text("password")
    val salt = Users.text("salt")
}
