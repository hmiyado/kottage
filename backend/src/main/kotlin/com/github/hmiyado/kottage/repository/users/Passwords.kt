package com.github.hmiyado.kottage.repository.users

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object Passwords : Table() {
    val user =
        Passwords.reference(
            "user",
            Users,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.NO_ACTION,
        )
    val password = text("password")
    val salt = text("salt")
}
