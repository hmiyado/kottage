package com.github.hmiyado.kottage.application.configuration

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class SessionSignKeyTest :
    DescribeSpec({
        describe("fromHex") {
            it("should decode a valid hex string into bytes") {
                // 32 hex chars: the minimum accepted length
                val key = SessionSignKey.fromHex("00ff10" + "00".repeat(13))
                key.bytes shouldBe byteArrayOf(0x00, 0xff.toByte(), 0x10) + ByteArray(13)
            }

            it("should accept a real openssl rand -hex 32 style key") {
                val hex = "a".repeat(64)
                val key = SessionSignKey.fromHex(hex)
                key.bytes.size shouldBe 32
            }

            it("should fail fast when null") {
                shouldThrow<SessionSignKeyConfigurationException> {
                    SessionSignKey.fromHex(null)
                }
            }

            it("should fail fast when blank") {
                shouldThrow<SessionSignKeyConfigurationException> {
                    SessionSignKey.fromHex("   ")
                }
            }

            it("should fail fast when shorter than the minimum length") {
                shouldThrow<SessionSignKeyConfigurationException> {
                    SessionSignKey.fromHex("ab".repeat(10))
                }
            }

            it("should fail fast when not a hex string") {
                shouldThrow<SessionSignKeyConfigurationException> {
                    // even length, long enough to pass the length check, but 'g' is not hex
                    SessionSignKey.fromHex("g".repeat(40))
                }
            }
        }
    })
