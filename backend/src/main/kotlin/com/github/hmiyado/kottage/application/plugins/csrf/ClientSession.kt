package com.github.hmiyado.kottage.application.plugins.csrf

import io.github.hmiyado.ktor.csrfprotection.CsrfTokenBoundClient
import kotlinx.serialization.Serializable

@Serializable
data class ClientSession(
    val token: String,
) : CsrfTokenBoundClient {
    override val representation: String = token
}
