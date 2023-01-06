package com.github.hmiyado.kottage.route.oauth

import com.auth0.jwt.interfaces.DecodedJWT
import com.github.hmiyado.kottage.model.OidcToken
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.service.oauth.OauthGoogleService
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import java.time.ZoneOffset

class OauthGoogleLocation(
    private val usersService: UsersService,
    private val oauthGoogleService: OauthGoogleService,
    private val oauthRedirects: MutableMap<String, String>,
) : Router {

    override fun addRoute(route: Route) {
        with(route) {
            authenticate("oidc-google") {
                get("/oauth/google/login") {
                    // Redirects to 'authorizeUrl' automatically
                    // If there is query parameter 'redirectUrl', redirect to it after oauth.
                    // If there is no 'redirectUrl', default redirect is set in AuthenticationOidcGoogle.kt
                }

                get("/oauth/google/callback") {
                    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                    val idToken = principal?.extraParameters?.get("id_token") ?: ""
                    val jwt = oauthGoogleService.verifyIdToken(idToken)
                    val oidcToken = jwt.toOidcToken()
                    val existingUser = usersService.getUser(oidcToken)
                    val user = existingUser ?: usersService.createUserByOidc(jwt.toOidcToken())
                    val userSession = call.sessions.get<UserSession>()
                    val result = when {
                        userSession?.id != null && userSession.id != user.id -> {
                            // already signed in as another user
                            // this request is strange
                            "conflict_session"
                        }

                        existingUser == user -> {
                            call.sessions.set(UserSession(id = user.id))
                            "signIn"
                        }

                        else -> {
                            // existingUser != user
                            call.sessions.set(UserSession(id = user.id))
                            "signUp"
                        }
                    }

                    val redirect = principal?.state?.let {
                        val redirect = oauthRedirects[it]
                        oauthRedirects.remove(it)
                        redirect
                    } ?: "http://localhost:3000"
                    val redirectWithResult = "$redirect?result=$result"
                    call.respondRedirect(redirectWithResult)
                }
            }
        }
    }

    private fun DecodedJWT.toOidcToken(): OidcToken {
        return OidcToken(
            issuer,
            subject,
            audience.joinToString(","),
            expiresAtAsInstant.atZone(ZoneOffset.UTC),
            issuedAtAsInstant.atZone(ZoneOffset.UTC),
        )
    }
}
