package com.github.hmiyado.kottage.model

import kotlinx.serialization.Serializable

@Serializable
data class Health(
    val description: String = "description",
    val version: Version = Version(""),
    val databaseType: DatabaseType = DatabaseType(""),
) {
    @JvmInline
    @Serializable
    value class Version(val value: String)

    @JvmInline
    @Serializable
    value class DatabaseType(val value: String)
}
