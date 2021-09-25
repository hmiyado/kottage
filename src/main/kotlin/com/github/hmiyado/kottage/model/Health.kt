package com.github.hmiyado.kottage.model

import kotlinx.serialization.Serializable

@Serializable
data class Health(
    val description: String = "description",
    val version: Version = Version(""),
) {
    @JvmInline
    @Serializable
    value class Version(val value: String)
}
