package com.github.hmiyado.kottage.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val id: Long = 0,
)
