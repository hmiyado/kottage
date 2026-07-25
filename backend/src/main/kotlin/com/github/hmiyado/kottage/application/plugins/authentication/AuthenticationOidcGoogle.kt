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
import io.ktor.util.generateNonce
import kotlinx.coroutines.runBlocking
import java.time.Instant

fun AuthenticationConfig.oidcGoogle(
    httpClient: HttpClient,
    oauthGoogle: OauthGoogle,
    oauthGoogleRepository: OauthGoogleRepository,
    nonceManager: NonceManager,
    oauthStateCodec: OauthStateCodec,
) {
    oauth("oidc-google") {
        val config =
            runBlocking {
                oauthGoogleRepository.getConfig()
            }
        urlProvider = { oauthGoogle.callbackUrl }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = config.authorizationEndpoint,
                authorizeUrlInterceptor = {
                    // Ktor already appended a `state` built from nonceManager.newNonce() to
                    // `parameters` before this interceptor runs; it is replaced here with a
                    // self-contained, HMAC-signed token carrying what the callback needs
                    // (redirectUrl / userId / OIDC nonce), since a server-side map keyed by the
                    // old opaque state cannot survive the authorize and callback requests landing
                    // on two different Lambda execution environments.
                    val oidcNonce = generateNonce()
                    val preOauthState =
                        PreOauthState(
                            redirectUrl = it.queryParameters["redirectUrl"] ?: oauthGoogle.defaultRedirectUrl,
                            userId =
                                it.call.sessions
                                    .get<UserSession>()
                                    ?.id,
                            nonce = oidcNonce,
                            expiresAt = Instant.now().plus(oauthStateExpiration).toEpochMilli(),
                        )
                    parameters.set("state", oauthStateCodec.encode(preOauthState))
                    parameters.append("nonce", oidcNonce)
                },
                accessTokenUrl = config.tokenEndpoint,
                requestMethod = HttpMethod.Post,
                clientId = oauthGoogle.clientId,
                clientSecret = oauthGoogle.clientSecret,
                defaultScopes = listOf("openid"),
                nonceManager = nonceManager,
                // response_type=code by default
                extraAuthParameters = listOf(),
                onStateCreated = { _, _ -> },
            )
        }
        client = httpClient
    }
}
