package com.github.hmiyado

interface Dependencies {
    val implementations: List<String>
    val testImplementations: List<String>

    object Kottage {
        private val dependencies = listOf(
            PostgreSql, MySql, Flyway, Redis, JUnit, Kotest, Mockk
        )
        val implementations = dependencies.flatMap { it.implementations }
        val testImplementations = dependencies.flatMap { it.testImplementations }
    }

    object Kotlin {
        const val jvmTarget = "11"
    }

    private object PostgreSql : Dependencies {
        private const val version = "42.3.1"
        private const val core = "org.postgresql:postgresql:$version"
        override val implementations: List<String> = listOf(core)
        override val testImplementations: List<String> = emptyList()
    }

    private object MySql : Dependencies {
        private const val version = "5.1.48"
        private const val core = "mysql:mysql-connector-java:$version"
        override val implementations: List<String> = listOf(core)
        override val testImplementations: List<String> = emptyList()
    }

    private object Flyway : Dependencies {
        private const val version = "8.0.4"
        private const val core = "org.flywaydb:flyway-core:$version"
        override val implementations: List<String> = listOf(core)
        override val testImplementations: List<String> = emptyList()
    }

    private object Redis : Dependencies {
        private const val version = "3.7.0"
        private const val jedis = "redis.clients:jedis:$version"
        override val implementations: List<String> = listOf(jedis)
        override val testImplementations: List<String> = emptyList()
    }

    private object JUnit : Dependencies {
        private const val version = "5.8.2"
        private const val jupiter = "org.junit.jupiter:junit-jupiter-api:$version"
        override val implementations: List<String> = emptyList()
        override val testImplementations: List<String> = listOf(jupiter)
    }

    private object Kotest : Dependencies {
        private const val version = "5.3.1"
        private const val jUnit5 = "io.kotest:kotest-runner-junit5:$version"
        private const val json = "io.kotest:kotest-assertions-json:$version"
        private const val dataTest = "io.kotest:kotest-framework-datatest:$version"
        private const val koinExtensionVersion = "1.1.0"
        private const val koin = "io.kotest.extensions:kotest-extensions-koin:$koinExtensionVersion"
        override val implementations: List<String> = emptyList()
        override val testImplementations: List<String> = listOf(jUnit5, json, dataTest, koin)
    }

    private object Mockk : Dependencies {
        private const val version = "1.12.2"
        private const val core = "io.mockk:mockk:$version"
        override val implementations: List<String> = emptyList()
        override val testImplementations: List<String> = listOf(core)
    }

    object Karate : Dependencies {
        private const val version = "1.1.0"
        private const val junit5 = "com.intuit.karate:karate-junit5:$version"
        override val implementations: List<String> = emptyList()
        override val testImplementations: List<String> = listOf(junit5)
    }
}
