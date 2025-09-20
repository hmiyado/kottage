package com.github.hmiyado.kottage.repository.oauth

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class OauthGoogleRepository(
    private val httpClient: HttpClient,
) {
    private val jsonDecoder =
        Json {
            ignoreUnknownKeys = true
        }

    suspend fun getConfig(): OpenIdGoogleConfig {
        val response = httpClient.get(GOOGLE_OIDC_DISCOVERY_DOCUMENT_URL)
        val responseText = response.bodyAsText()
        return jsonDecoder.decodeFromString(responseText)
    }

    companion object {
        private const val GOOGLE_OIDC_DISCOVERY_DOCUMENT_URL = "https://accounts.google.com/.well-known/openid-configuration"
    }
}

/**
 * @see google https://developers.google.com/identity/openid-connect/openid-connect#discovery
 */
@Serializable
data class OpenIdGoogleConfig(
    val issuer: String,
    @SerialName("authorization_endpoint")
    val authorizationEndpoint: String,
    @SerialName("token_endpoint")
    val tokenEndpoint: String,
    @SerialName("jwks_uri")
    val jwksUri: String,
)
