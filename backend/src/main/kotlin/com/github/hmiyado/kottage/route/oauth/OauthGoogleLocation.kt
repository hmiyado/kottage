package com.github.hmiyado.kottage.route.oauth

import com.auth0.jwt.interfaces.DecodedJWT
import com.github.hmiyado.kottage.application.configuration.OauthGoogle
import com.github.hmiyado.kottage.application.plugins.authentication.OauthStateCodec
import com.github.hmiyado.kottage.application.plugins.authentication.newUserSession
import com.github.hmiyado.kottage.model.OidcToken
import com.github.hmiyado.kottage.repository.oauth.OauthGoogleRepository
import com.github.hmiyado.kottage.repository.users.UserRepository
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
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import java.time.ZoneOffset

class OauthGoogleLocation(
    private val usersService: UsersService,
    private val oauthGoogle: OauthGoogle,
    private val oauthGoogleRepository: OauthGoogleRepository,
    private val oauthGoogleService: OauthGoogleService,
    private val oauthStateCodec: OauthStateCodec,
) : Router {
    override fun addRoute(route: Route) {
        with(route) {
            authenticate("oidc-google") {
                get("/oauth/google/authorize") {
                    // Redirects to 'authorizeUrl' automatically
                    // If there is query parameter 'redirectUrl', redirect to it after oauth.
                    // If there is no 'redirectUrl', default redirect is set in AuthenticationOidcGoogle.kt
                }

                get("/oauth/google/callback") {
                    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                    val preOauthState = principal?.state?.let { oauthStateCodec.decode(it) }
                    val oidcToken =
                        run {
                            val idToken = principal?.extraParameters?.get("id_token") ?: ""
                            val config = oauthGoogleRepository.getConfig()
                            val jwt =
                                oauthGoogleService.verifyIdToken(
                                    idToken,
                                    config.issuer,
                                    oauthGoogle.clientId,
                                    preOauthState?.nonce ?: "",
                                )
                            jwt.toOidcToken()
                        }
                    val existingUser = usersService.getUser(oidcToken)
                    val result =
                        when {
                            existingUser != null -> {
                                when {
                                    preOauthState?.userId == null -> {
                                        // newly signed in
                                        call.sessions.set(newUserSession(existingUser.id))
                                        "signIn"
                                    }

                                    existingUser.id == preOauthState.userId -> {
                                        // already signed in via oidc user
                                        call.sessions.set(newUserSession(existingUser.id))
                                        "alreadySignIn"
                                    }

                                    else -> {
                                        // already signed in as another user
                                        // this request is strange
                                        "conflictSession"
                                    }
                                }
                            }

                            preOauthState?.userId == null -> {
                                val user = usersService.createUserByOidc(oidcToken)
                                call.sessions.set(newUserSession(user.id))
                                "signUp"
                            }

                            else -> {
                                // existingUser == null && preOauthState?.userId != null
                                kotlin
                                    .runCatching {
                                        usersService.connectOidc(preOauthState.userId, oidcToken)
                                        "connect"
                                    }.getOrElse {
                                        when (it) {
                                            is UserRepository.ConflictOidcTokenException -> "conflictAccount"
                                            else -> "error"
                                        }
                                    }
                            }
                        }

                    val redirect = preOauthState?.redirectUrl ?: oauthGoogle.defaultRedirectUrl
                    val redirectWithResult = "$redirect?result=$result"
                    call.respondRedirect(redirectWithResult)
                }
            }
        }
    }

    private fun DecodedJWT.toOidcToken(): OidcToken =
        OidcToken(
            issuer,
            subject,
            audience.joinToString(","),
            expiresAtAsInstant.atZone(ZoneOffset.UTC),
            issuedAtAsInstant.atZone(ZoneOffset.UTC),
        )
}
