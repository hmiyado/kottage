package com.github.hmiyado.application

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DatabaseConfigurationTest : DescribeSpec() {
    init {
        describe("detectConfiguration") {
            it("should return Postgres when there are all properties") {
                val actual = DatabaseConfiguration.detectConfiguration(
                    "name",
                    "host",
                    "user",
                    "password"
                )
                actual shouldBe DatabaseConfiguration.Postgres("name", "host", "user", "password")
            }

            describe("should return Memory when there are null or empty postgres properties") {
                forAll(
                    DatabaseConfigurationParameter(null, "host", "user", "password"),
                    DatabaseConfigurationParameter("name", null, "user", "password"),
                    DatabaseConfigurationParameter("name", "host", null, "password"),
                    DatabaseConfigurationParameter("name", "host", "user", null),
                    DatabaseConfigurationParameter("", "host", "user", "password"),
                    DatabaseConfigurationParameter("name", "", "user", "password"),
                    DatabaseConfigurationParameter("name", "host", "", "password"),
                    DatabaseConfigurationParameter("name", "host", "user", ""),
                ) { (name, host, user, password) ->
                    val actual = DatabaseConfiguration.detectConfiguration(
                        name,
                        host,
                        user,
                        password
                    )
                    actual shouldBe DatabaseConfiguration.Memory
                }
            }
        }
    }

    private data class DatabaseConfigurationParameter(
        val postgresName: String?,
        val postgresHost: String?,
        val postgresUser: String?,
        val postgresPassword: String?,
    )
}
