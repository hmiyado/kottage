package com.github.hmiyado.kottage.service.oauth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.RSAKeyProvider
import com.github.hmiyado.kottage.repository.oauth.OauthGoogleRepository
import java.net.URL
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

interface OauthGoogleService {
    suspend fun verifyIdToken(idToken: String): DecodedJWT
}

class OauthGoogleServiceImpl(
    private val oauthGoogleRepository: OauthGoogleRepository,
) : OauthGoogleService {
    override suspend fun verifyIdToken(idToken: String): DecodedJWT {
        val config = oauthGoogleRepository.getConfig()
        val alg = Algorithm.RSA256(createRsaKeyProvider(config.jwksUri))
        return JWT.require(alg)
            .build()
            .verify(idToken)
    }

    private fun createRsaKeyProvider(jwkUrl: String): RSAKeyProvider {
        val provider = JwkProviderBuilder(URL(jwkUrl))
            .build()
        return object : RSAKeyProvider {
            override fun getPublicKeyById(keyId: String?): RSAPublicKey {
                return (provider.get(keyId).publicKey as RSAPublicKey)
            }

            override fun getPrivateKey(): RSAPrivateKey {
                // Do nothing because this RSAKeyProvider is used for only verifying
                throw IllegalStateException("")
            }

            override fun getPrivateKeyId(): String {
                // Do nothing because this RSAKeyProvider is used for only verifying
                throw IllegalStateException("")
            }

        }
    }
}
