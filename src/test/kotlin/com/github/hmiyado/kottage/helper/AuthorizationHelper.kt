package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.plugins.authentication.admin
import com.github.hmiyado.kottage.application.plugins.authentication.users
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.testing.TestApplicationRequest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import org.koin.ktor.ext.get

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
            oauth("oidc-google") {
                urlProvider = { "http://localhost:8080/oauth/google/callback" }
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(name = "google", authorizeUrl = "", accessTokenUrl = "", clientId = "", clientSecret = "")
                }
                client = application.get()
            }
        }
    }

    fun authorizeAsUserAndAdmin(
        request: TestApplicationRequest,
        user: User,
    ) {
        val session = "this-is-mocked-admin-session"
        coEvery { sessionStorage.write(any(), any()) } just Runs
        coEvery { sessionStorage.read(any()) } returns "id=#l${user.id}"
        every { usersService.getUser(user.id) } returns user
        adminsService?.let { service -> every { service.isAdmin(user.id) } returns true }
        request.addHeader("Cookie", "user_session=$session")
    }

    fun authorizeAsUser(
        request: TestApplicationRequest,
        user: User,
    ) {
        val session = "example0session"
        coEvery { sessionStorage.write(any(), any()) } just Runs
        coEvery { sessionStorage.read(session) }.returns("id=#l${user.id}")
        coEvery {
            sessionStorage.read(not(session))
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
            coEvery { sessionStorage.read(any()) }.returns("id=#l${user.id}")
            every { usersService.getUser(user.id) } returns user
            every { adminsService.isAdmin(user.id) } returns true
            request.addHeader("Cookie", "user_session=$session")
        }
    }
}
