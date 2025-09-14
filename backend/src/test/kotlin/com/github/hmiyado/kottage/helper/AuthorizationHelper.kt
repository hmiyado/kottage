package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.plugins.authentication.admin
import com.github.hmiyado.kottage.application.plugins.authentication.users
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.model.toJsonString
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just

class AuthorizationHelper(
    private val usersService: UsersService,
    private val sessionStorage: SessionStorage,
    private val adminsService: AdminsService? = null,
) {
    fun installSessionAuthentication(application: Application) = with(application) {
        install(Sessions) {
            cookie<UserSession>("user_session", storage = sessionStorage)
        }

        install(Authentication) {
            users(usersService)
            adminsService?.let {
                admin(usersService, it)
            }
            oauth("oidc-google") {
                client = HttpClient()
                urlProvider = { "http://localhost:8080/oauth/google/callback" }
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = "google",
                        authorizeUrl = "",
                        accessTokenUrl = "",
                        clientId = "",
                        clientSecret = "",
                    )
                }
            }
        }
    }

    fun authorizeAsUserAndAdmin(
        builder: HttpRequestBuilder,
        user: User,
    ) {
        val session = "this-is-mocked-admin-session"
        coEvery { sessionStorage.write(any(), any()) } just Runs
        coEvery { sessionStorage.read(any()) } returns UserSession(user.id).toJsonString()
        every { usersService.getUser(user.id) } returns user
        adminsService?.let { service -> every { service.isAdmin(user.id) } returns true }
        builder.headers {
            append("Cookie", "user_session=$session")
        }
    }
}

fun HttpRequestBuilder.authorizeAsUserAndAdmin(
    helper: AuthorizationHelper,
    user: User,
) {
    helper.authorizeAsUserAndAdmin(this, user)
}
