package com.github.hmiyado.kottage.application.plugins.clientsession

import kotlinx.serialization.Serializable

@Serializable
data class ClientSession(
    val token: String,
)
