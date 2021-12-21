package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.plugins.authentication.admin
import com.github.hmiyado.kottage.application.plugins.authentication.users
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just

class AuthorizationHelper(
    private val usersService: UsersService,
    private val sessionStorage: SessionStorage,
    private val adminsService: AdminsService? = null,
) {

    fun installSessionAuthentication(application: Application) {
        application.install(Sessions) {
            cookie<UserSession>("user_session", storage = sessionStorage)
        }

        application.install(Authentication) {
            users(usersService)
            adminsService?.let {
                admin(usersService, it)
            }
        }
    }

    fun authorizeAsUserAndAdmin(
        request: TestApplicationRequest,
        user: User,
    ) {
        val session = "this-is-mocked-admin-session"
        coEvery { sessionStorage.write(any(), any()) } just Runs
        coEvery { sessionStorage.read<UserSession>(any(), any()) }.returns(UserSession(user.id))
        every { usersService.getUser(user.id) } returns user
        adminsService?.let { service -> every { service.isAdmin(user.id) } returns true }
        request.addHeader("Cookie", "user_session=$session")
    }

    fun authorizeAsUser(
        request: TestApplicationRequest,
        user: User
    ) {
        val session = "example0session"
        coEvery { sessionStorage.write(any(), any()) } just Runs
        coEvery { sessionStorage.read<UserSession>(session, any()) }.returns(UserSession(user.id))
        coEvery {
            sessionStorage.read<UserSession>(
                not(session),
                any()
            )
        }.throws(NoSuchElementException("session not found"))
        every { usersService.getUser(user.id) } returns user
        request.addHeader("Cookie", "user_session=$session")
    }

    companion object {

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
    }
}
