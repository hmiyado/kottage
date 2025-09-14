package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.plugins.csrf.ClientSession
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfTokenSession
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.session
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

fun AuthenticationConfig.csrf() {
    session<CsrfTokenSession>(name = "CsrfTokenSession") {
        validate {
            val csrfTokenSession = this.sessions.get<CsrfTokenSession>() ?: return@validate null
            val clientSession = this.sessions.get<ClientSession>() ?: return@validate null
            return@validate if (csrfTokenSession.clientSession == clientSession) {
                csrfTokenSession
            } else {
                null
            }
        }
    }
}
