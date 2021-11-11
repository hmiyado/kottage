package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.auth.Authentication
import io.ktor.auth.session
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Authentication.Configuration.admin(usersService: UsersService, adminsService: AdminsService) {
    session<UserSession>(name = "admin") {
        validate {
            val session = this.sessions.get<UserSession>() ?: return@validate null
            if (adminsService.isAdmin(session.id)) {
                val user = usersService.getUser(session.id) ?: return@validate null
                UserPrincipal.Admin(user)
            } else {
                null
            }
        }
    }
}
