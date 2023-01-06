package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.OauthGoogle
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.repository.oauth.OauthGoogleRepository
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.util.NonceManager
import kotlinx.coroutines.runBlocking

fun AuthenticationConfig.oidcGoogle(
    httpClient: HttpClient,
    oauthGoogle: OauthGoogle,
    oauthGoogleRepository: OauthGoogleRepository,
    nonceManager: NonceManager,
    preOauthStates: MutableMap<String, PreOauthState>,
) {
    oauth("oidc-google") {
        val config = runBlocking {
            oauthGoogleRepository.getConfig()
        }
        // todo: distinct dev and production host
        urlProvider = { "http://localhost:8080/oauth/google/callback" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = config.authorizationEndpoint,
                accessTokenUrl = config.tokenEndpoint,
                requestMethod = HttpMethod.Post,
                clientId = oauthGoogle.clientId,
                clientSecret = oauthGoogle.clientSecret,
                defaultScopes = listOf("openid"),
                nonceManager = nonceManager,
                // response_type=code by default
                extraAuthParameters = listOf(),
                onStateCreated = { call, state ->
                    preOauthStates[state] = PreOauthState(
                        // todo: distinct dev and production host
                        redirectUrl = call.request.queryParameters["redirectUrl"] ?: "http://localhost:3000",
                        userId = call.sessions.get<UserSession>()?.id,
                    )
                },
            )
        }
        client = httpClient
    }
}
