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
            val user = usersService.getUser(session.id) ?: return@validate null
            UserPrincipal.User(user)
        }
    }
}
