package com.github.hmiyado.kottage.route.oauth

import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.service.oauth.OauthGoogleService
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

class OauthGoogleLocation(
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
                    val redirect = principal?.state?.let {
                        val redirect = oauthRedirects[it]
                        oauthRedirects.remove(it)
                        redirect
                    } ?: "https://miyado.dev"
                    call.respondText {
                        """
                        redirect=$redirect
                        accessToken=${principal?.accessToken}
                        expiresIn=${principal?.expiresIn}
                        extraParameters=${principal?.extraParameters}
                        refreshToken=${principal?.refreshToken}
                        state=${principal?.state}
                        tokenType=${principal?.tokenType}
                        extraParameter.id_token=${idToken}
                        # JWT
                        header=${jwt.header}
                        payload=${jwt.payload}
                        signature=${jwt.signature}
                        token=${jwt.token}
                        issuer=${jwt.issuer}
                        subject=${jwt.subject}
                    """.trimIndent()
                    }
//                    call.respondRedirect(redirect!!)
                }
            }
        }
    }
}
