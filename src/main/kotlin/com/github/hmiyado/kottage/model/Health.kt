package com.github.hmiyado.kottage.model

import kotlinx.serialization.Serializable
import com.github.hmiyado.kottage.openapi.models.Health as OpenApiHealth

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

fun Health.toOpenApiHealth(): OpenApiHealth = OpenApiHealth(
    description = description,
    version = version.value,
    databaseType = databaseType.value
)
