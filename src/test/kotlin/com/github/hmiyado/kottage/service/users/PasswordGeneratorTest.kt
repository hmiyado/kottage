package com.github.hmiyado.kottage.service.users

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class PasswordGeneratorTest : DescribeSpec({
    describe("PasswordGenerator") {
        it("should generate secure password") {
            val actual = PasswordGenerator.generateSecurePassword("raw password", "salt")
            actual shouldBe Password("272eb9472c47278ef49cb1a231e06351d2a022d469c9cae20579f4c61e1162f1e703a5247bac10bae5d30400bfa76d59781552a95d8165c39157df46eb870ad9")
        }
        it("should generate different secure passwords with different salts") {
            val rawPassword = "raw password"
            val actual1 = PasswordGenerator.generateSecurePassword(rawPassword, "salt1")
            val actual2 = PasswordGenerator.generateSecurePassword(rawPassword, "salt2")
            actual1 shouldNotBe actual2
        }
        it("should generate same passwords with same password and salts") {
            val rawPassword = "raw password"
            val actual1 = PasswordGenerator.generateSecurePassword(rawPassword, "salt")
            val actual2 = PasswordGenerator.generateSecurePassword(rawPassword, "salt")
            actual1 shouldBe actual2
        }
    }
})
