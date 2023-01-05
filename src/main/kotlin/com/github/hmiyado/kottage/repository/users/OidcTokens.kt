package com.github.hmiyado.kottage.repository.users

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OidcTokens : Table() {
    val user = OidcTokens.reference(
        "user",
        Users,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION,
    )
    val issuer = text("issuer")
    val subject = varchar("subject", 255)
    val audience = text("audience")
    val expiration = datetime("expiration")
    val issuedAt = datetime("issuedAt")
}
