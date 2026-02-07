package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object Admins : Table() {
    val user =
        Admins.reference(
            "user",
            Users,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.NO_ACTION,
        )
}
