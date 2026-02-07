package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.OidcToken
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import org.jetbrains.exposed.v1.jdbc.insert
import java.time.ZoneOffset

object OidcTokens : Table() {
    val user =
        OidcTokens.reference(
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

    fun insert(
        id: Long,
        token: OidcToken,
    ) {
        OidcTokens.insert {
            it[user] = id
            it[issuer] = token.issuer
            it[subject] = token.subject
            it[audience] = token.audience
            it[expiration] = token.expiration.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
            it[issuedAt] = token.issuedAt.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
        }
    }
}
