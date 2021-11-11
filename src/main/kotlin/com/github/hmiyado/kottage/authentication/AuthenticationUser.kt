package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.auth.Authentication
import io.ktor.auth.session
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Authentication.Configuration.users(usersService: UsersService) {
    session<UserSession>(name = "user") {
        validate {
            val session = this.sessions.get<UserSession>() ?: return@validate null
            val user = usersService.getUser(session.id) ?: return@validate null
            UserPrincipal(user)
        }
    }
}
