package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.OauthGoogle
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.util.GenerateOnlyNonceManager

fun AuthenticationConfig.oidcGoogle(httpClient: HttpClient, oauthGoogle: OauthGoogle, redirects: MutableMap<String, String>) {
    oauth("oidc-google") {
        // todo: distinct dev and production host
        urlProvider = { "http://localhost:8080/oauth/google/callback" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
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
