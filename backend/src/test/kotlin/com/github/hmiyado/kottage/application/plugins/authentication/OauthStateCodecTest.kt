package com.github.hmiyado.kottage.application.plugins.authentication

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import java.time.Instant

class OauthStateCodecTest :
    DescribeSpec({
        val signKey = ByteArray(32) { it.toByte() }
        val otherSignKey = ByteArray(32) { (it + 1).toByte() }

        fun futureState(userId: Long? = null) =
            PreOauthState(
                redirectUrl = "https://miyado.dev/callback",
                userId = userId,
                nonce = "some-nonce",
                expiresAt = Instant.now().plusSeconds(180).toEpochMilli(),
            )

        describe("encode/decode") {
            it("should round trip a valid, unexpired state") {
                val codec = OauthStateCodec(signKey)
                val state = futureState(userId = 42)

                val token = codec.encode(state)
                val decoded = codec.decode(token)

                decoded shouldBe state
            }

            it("should round trip when userId is null (not signed in)") {
                val codec = OauthStateCodec(signKey)
                val state = futureState(userId = null)

                codec.decode(codec.encode(state)) shouldBe state
            }
        }

        describe("decode") {
            it("should reject a token signed with a different key") {
                val token = OauthStateCodec(otherSignKey).encode(futureState())

                OauthStateCodec(signKey).decode(token) shouldBe null
            }

            it("should reject a tampered payload") {
                val codec = OauthStateCodec(signKey)
                val token = codec.encode(futureState())
                val (payload, signature) = token.split(".", limit = 2)
                val tampered = "$payload-tampered.$signature"

                codec.decode(tampered) shouldBe null
            }

            it("should reject an expired token") {
                val codec = OauthStateCodec(signKey)
                val expired =
                    PreOauthState(
                        redirectUrl = "https://miyado.dev/callback",
                        userId = null,
                        nonce = "some-nonce",
                        expiresAt = Instant.now().minusSeconds(1).toEpochMilli(),
                    )

                codec.decode(codec.encode(expired)) shouldBe null
            }

            it("should reject a malformed token") {
                val codec = OauthStateCodec(signKey)

                codec.decode("not-a-valid-token") shouldBe null
            }

            it("should verify via the NonceManager adapter") {
                val codec = OauthStateCodec(signKey)
                val nonceManager = OauthStateNonceManager(codec)
                val token = codec.encode(futureState())

                runBlocking {
                    nonceManager.verifyNonce(token) shouldBe true
                    nonceManager.verifyNonce("garbage") shouldBe false
                    (nonceManager.newNonce().isNotEmpty()) shouldBe true
                }
            }
        }
    })
