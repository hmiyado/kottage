package com.github.hmiyado

interface Dependencies {
    val implementations: List<String>
    val testImplementations: List<String>

    object Kottage {
        private val dependencies = listOf(
            Logback, Ktor, KtorClient, Miyado, Exposed, Koin, PostgreSql, MySql, Flyway, Redis, JUnit, Kotest, Mockk
        )
        val implementations = dependencies.flatMap { it.implementations }
        val testImplementations = dependencies.flatMap { it.testImplementations }
    }

    object Kotlin {
        const val jvmTarget = "11"
    }

    private object Logback : Dependencies {
        private const val classic = "ch.qos.logback:logback-classic:1.2.10"
        private const val janino = "org.codehaus.janino:janino:3.1.6"
        override val implementations: List<String> = listOf(classic, janino)
        override val testImplementations: List<String> = emptyList()
    }

    private object Ktor : Dependencies {
        const val version = "2.0.1"
        private const val serverNetty = "io.ktor:ktor-server-netty:$version"
        private const val test = "io.ktor:ktor-server-tests:$version"
        private const val auth = "io.ktor:ktor-server-auth:$version"
        private const val autoHeadResponse = "io.ktor:ktor-server-auto-head-response:$version"
        private const val callLogging = "io.ktor:ktor-server-call-logging:$version"
        private const val contentNegotiation = "io.ktor:ktor-server-content-negotiation:$version"
        private const val cors = "io.ktor:ktor-server-cors:$version"
        private const val defaultHeaders = "io.ktor:ktor-server-default-headers:$version"
        private const val statusPages = "io.ktor:ktor-server-status-pages:$version"
        private const val serializationJson = "io.ktor:ktor-serialization-kotlinx-json:$version"
        private const val sessions = "io.ktor:ktor-server-sessions:$version"
        override val implementations: List<String> = listOf(
            serverNetty,
            test,
            auth,
            autoHeadResponse,
            callLogging,
            contentNegotiation,
            cors,
            defaultHeaders,
            statusPages,
            serializationJson,
            sessions
        )
        override val testImplementations: List<String> = emptyList()
    }

    private object KtorClient : Dependencies {
        private const val version = Ktor.version
        private const val core = "io.ktor:ktor-client-core:$version"
        private const val cio = "io.ktor:ktor-client-cio:$version"
        private const val mock = "io.ktor:ktor-client-mock:$version"
        override val implementations: List<String> = listOf(core, cio)
        override val testImplementations: List<String> = listOf(mock)
    }

    private object Miyado : Dependencies {
        private const val csrfProtection = "io.github.hmiyado:ktor-csrf-protection:2.0.0"
        override val implementations: List<String> = listOf(csrfProtection)
        override val testImplementations: List<String> = emptyList()
    }

    private object Exposed : Dependencies {
        private const val version = "0.36.2"
        private const val core = "org.jetbrains.exposed:exposed-core:$version"
        private const val dao = "org.jetbrains.exposed:exposed-dao:$version"
        private const val jdbc = "org.jetbrains.exposed:exposed-jdbc:$version"
        private const val javaTime = "org.jetbrains.exposed:exposed-java-time:$version"
        override val implementations: List<String> = listOf(core, dao, jdbc, javaTime)
        override val testImplementations: List<String> = emptyList()
    }

    private object Koin : Dependencies {
        private const val version = "3.2.0"
        private const val ktor = "io.insert-koin:koin-ktor:$version"
        private const val test = "io.insert-koin:koin-test:$version"
        override val implementations: List<String> = listOf(ktor)
        override val testImplementations: List<String> = listOf(test)
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
