package com.github.hmiyado.kottage.application.plugins.csrf

data class ClientSession(
    val token: String,
) : CsrfTokenBoundClient {
    override val representation: String = token
}
