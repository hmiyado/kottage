package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.csrfprotection.CsrfTokenBoundClient

data class ClientSession(
    val token: String,
) : CsrfTokenBoundClient {
    override val representation: String = token
}
