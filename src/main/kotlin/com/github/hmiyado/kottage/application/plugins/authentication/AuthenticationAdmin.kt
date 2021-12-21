package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.auth.Authentication
import io.ktor.auth.session
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Authentication.Configuration.admin(
    usersService: UsersService,
    adminsService: AdminsService,
    authenticationConfiguration: AuthenticationConfiguration? = null
) {
    authenticationConfiguration?.adminCredential?.let { (adminName, adminPassword) ->
        val user = usersService.authenticateUser(adminName, adminPassword) ?: run {
            // create admin user if there is no admin
            usersService.createUser(adminName, adminPassword)
        }
        if (adminsService.isAdmin(user.id)) {
            return@let
        }
        adminsService.addAdmin(user)
    }

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
