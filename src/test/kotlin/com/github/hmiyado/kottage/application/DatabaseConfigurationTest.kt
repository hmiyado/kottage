package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.config.ApplicationConfig
import io.ktor.config.MapApplicationConfig

class DatabaseConfigurationTest : DescribeSpec() {
    init {
        describe("detectConfiguration") {
            it("should return Postgres when there are all properties") {
                val expected = DatabaseConfigurationParameter("name", "host", "user", "password")
                val config = createApplicationConfig("postgres", expected)
                val actual = DatabaseConfiguration.detectConfiguration(config)
                actual shouldBe DatabaseConfiguration.Postgres("name", "host", "user", "password")
            }

            it("should return MySql when there are all properties") {
                val expected = DatabaseConfigurationParameter("name", "host", "user", "password")
                val config = createApplicationConfig("mysql", expected)
                val actual = DatabaseConfiguration.detectConfiguration(config)
                actual shouldBe DatabaseConfiguration.MySql("name", "host", "user", "password")
            }

            describe("should return Memory when there are null or empty postgres properties") {
                withData(
                    DatabaseConfigurationParameter(null, "host", "user", "password"),
                    DatabaseConfigurationParameter("name", null, "user", "password"),
                    DatabaseConfigurationParameter("name", "host", null, "password"),
                    DatabaseConfigurationParameter("name", "host", "user", null),
                    DatabaseConfigurationParameter("", "host", "user", "password"),
                    DatabaseConfigurationParameter("name", "", "user", "password"),
                    DatabaseConfigurationParameter("name", "host", "", "password"),
                    DatabaseConfigurationParameter("name", "host", "user", ""),
                ) { parameter: DatabaseConfigurationParameter ->
                    val applicationConfig = createApplicationConfig("mysql", parameter)
                    val actual = DatabaseConfiguration.detectConfiguration(applicationConfig)
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
    ) : WithDataTestName {
        override fun dataTestName(): String = "(name, host, user, password)=($postgresName, $postgresHost, $postgresUser, $postgresPassword)"
    }

    private fun createApplicationConfig(path: String, parameter: DatabaseConfigurationParameter): ApplicationConfig {
        val keyValues = listOfNotNull(
            parameter.postgresName?.let { "name" to it },
            parameter.postgresHost?.let { "host" to it },
            parameter.postgresUser?.let { "user" to it },
            parameter.postgresPassword?.let { "password" to it },
        ).map { (k, v) ->
            "$path.$k" to v
        }

        return MapApplicationConfig(*(keyValues.toTypedArray()))
    }
}
