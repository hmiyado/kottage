package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.session
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.sessionId
import io.ktor.utils.io.readUTF8Line

fun Authentication.Configuration.users(usersService: UsersService, sessionStorage: SessionStorage) {
    session<UserSession>(name = "user") {
        validate {
            val sessionId = this.sessionId ?: return@validate null
            sessionStorage.read(sessionId) {
                val id = it.readUTF8Line()?.toLong() ?: return@read null
                val user = usersService.getUser(id) ?: return@read null
                UserIdPrincipal(user.id.toString())
            }
        }
    }
}
