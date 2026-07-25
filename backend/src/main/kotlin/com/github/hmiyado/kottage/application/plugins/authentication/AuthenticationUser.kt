package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.session
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

fun AuthenticationConfig.users(usersService: UsersService) {
    session<UserSession>(name = "user") {
        validate {
            val session = this.sessions.get<UserSession>() ?: return@validate null
            // The signature is already verified by SessionTransportTransformerMessageAuthentication
            // by the time we get here; expiresAt is part of the signed payload, so it still needs
            // an explicit check since there is no server-side store to have expired it for us.
            if (session.expiresAt < System.currentTimeMillis()) return@validate null
            val user = usersService.getUser(session.id) ?: return@validate null
            UserPrincipal.User(user)
        }
    }
}
