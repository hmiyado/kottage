package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.plugins.clientsession.ClientSession
import kotlinx.serialization.Serializable

@Serializable
data class CsrfTokenSession(
    val token: String,
    val clientSession: ClientSession,
)
