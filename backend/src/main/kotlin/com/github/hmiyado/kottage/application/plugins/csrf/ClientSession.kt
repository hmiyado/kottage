package com.github.hmiyado.kottage.application.plugins.csrf

import kotlinx.serialization.Serializable

@Serializable
data class ClientSession(
    val token: String,
)
