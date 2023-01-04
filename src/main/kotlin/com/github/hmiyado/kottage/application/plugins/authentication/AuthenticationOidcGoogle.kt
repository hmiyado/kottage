package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.OauthGoogle
import com.github.hmiyado.kottage.repository.oauth.OauthGoogleRepository
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.util.GenerateOnlyNonceManager
import kotlinx.coroutines.runBlocking

fun AuthenticationConfig.oidcGoogle(
    httpClient: HttpClient,
    oauthGoogle: OauthGoogle,
    oauthGoogleRepository: OauthGoogleRepository,
    redirects: MutableMap<String, String>,
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
                // todo: implement nonce manager
                nonceManager = GenerateOnlyNonceManager,
                // response_type=code by default
                extraAuthParameters = listOf(),
                onStateCreated = { call, state ->
                    redirects[state] = call.request.queryParameters["redirectUrl"]!!
                },
            )
        }
        client = httpClient
    }
}
