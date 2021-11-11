package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.authentication.admin
import com.github.hmiyado.kottage.authentication.users
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.basic
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import java.util.*

class AuthorizationHelper {
    companion object {
        private const val adminName = "admin"
        private const val adminPassword = "admin"
        private val adminBase64: String = Base64.getEncoder().encodeToString("$adminName:$adminPassword".toByteArray())
        private val authenticationHeaderBasicAdmin = "Basic $adminBase64"
        val adminCredential = UserPasswordCredential(adminName, adminPassword)

        fun installAuthentication(application: Application) {
            application.install(Authentication) {
                basic {
                    validate { (name, password) ->
                        if (name == adminName && password == adminName) {
                            return@validate UserIdPrincipal(adminName)
                        } else {
                            null
                        }
                    }
                }
            }
        }

        fun installSessionAuthentication(
            application: Application,
            usersService: UsersService,
            sessionStorage: SessionStorage,
            adminService: AdminsService? = null,
        ) {
            application.install(Sessions) {
                cookie<UserSession>("user_session", storage = sessionStorage)
            }

            application.install(Authentication) {
                users(usersService)
                adminService?.let {
                    admin(usersService, it)
                }
            }
        }

        fun authorizeAsUserAndAdmin(
            request: TestApplicationRequest,
            sessionStorage: SessionStorage,
            usersService: UsersService,
            adminsService: AdminsService,
            user: User,
        ) {
            val session = "this-is-mocked-admin-session"
            coEvery { sessionStorage.write(any(), any()) } just Runs
            coEvery { sessionStorage.read<UserSession>(any(), any()) }.returns(UserSession(user.id))
            every { usersService.getUser(user.id) } returns user
            every { adminsService.isAdmin(user.id) } returns true
            request.addHeader("Cookie", "user_session=$session")
        }

        fun authorizeAsUser(
            request: TestApplicationRequest,
            usersService: UsersService,
            sessionStorage: SessionStorage,
            user: User
        ) {
            val session = "this-is-mocked-user-session"
            coEvery { sessionStorage.write(any(), any()) } just Runs
            coEvery { sessionStorage.read<UserSession>(any(), any()) }.returns(UserSession(user.id))
            every { usersService.getUser(user.id) } returns user
            request.addHeader("Cookie", "user_session=$session")
        }
    }
}
