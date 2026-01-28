package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.session
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

fun AuthenticationConfig.admin(
    usersService: UsersService,
    adminsService: AdminsService,
    authenticationConfiguration: AuthenticationConfiguration? = null,
) {
    authenticationConfiguration?.adminCredential?.let { (adminName, adminPassword) ->
        // Check if admin user exists, create only if not exists
        val user =
            usersService.getUserByScreenName(adminName) ?: run {
                // create admin user if there is no admin
                try {
                    usersService.createUser(adminName, adminPassword)
                } catch (e: UsersService.DuplicateScreenNameException) {
                    // If duplicate, retrieve the existing user (race condition scenario)
                    usersService.getUserByScreenName(adminName)
                        ?: throw IllegalStateException("Admin user should exist but not found", e)
                }
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
