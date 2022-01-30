package com.github.hmiyado

object Dependencies {
    object Kotlin {
        const val jvmTarget = "11"
    }

    object Logback {
        const val classic = "ch.qos.logback:logback-classic:1.2.10"
        const val janino = "org.codehaus.janino:janino:3.1.6"
    }

    object Ktor {
        private const val version = "1.6.7"
        const val serverNetty = "io.ktor:ktor-server-netty:$version"
        const val serialization = "io.ktor:ktor-serialization:$version"
        const val test = "io.ktor:ktor-server-tests:$version"
        const val auth = "io.ktor:ktor-auth:$version"
        const val sessions = "io.ktor:ktor-server-sessions:$version"
    }

    object KtorClient {
        private const val version = "1.6.7"
        const val core = "io.ktor:ktor-client-core:$version"
        const val cio = "io.ktor:ktor-client-cio:$version"
        const val mock = "io.ktor:ktor-client-mock:$version"
    }

    object Miyado {
        const val csrfProtection = "io.github.hmiyado:ktor-csrf-protection:0.1"
    }

    object Exposed {
        private const val version = "0.36.2"
        const val core = "org.jetbrains.exposed:exposed-core:$version"
        const val dao = "org.jetbrains.exposed:exposed-dao:$version"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc:$version"
        const val javaTime = "org.jetbrains.exposed:exposed-java-time:$version"
    }

    object Koin {
        private const val version = "3.1.5"
        const val ktor = "io.insert-koin:koin-ktor:$version"
        const val test = "io.insert-koin:koin-test:$version"
    }

    object PostgreSql {
        private const val version = "42.3.1"
        const val core = "org.postgresql:postgresql:$version"
    }

    object MySql {
        private const val version = "5.1.48"
        const val core = "mysql:mysql-connector-java:$version"
    }

    object Flyway {
        private const val version = "8.0.4"
        const val core = "org.flywaydb:flyway-core:$version"
    }

    object Redis {
        private const val version = "3.7.0"
        const val jedis = "redis.clients:jedis:$version"
    }

    object JUnit {
        private const val version = "5.8.2"
        const val jupiter = "org.junit.jupiter:junit-jupiter-api:$version"
    }

    object Kotest {
        private const val version = "5.1.0"
        const val jUnit5 = "io.kotest:kotest-runner-junit5:$version"
        const val json = "io.kotest:kotest-assertions-json:$version"
        const val dataTest = "io.kotest:kotest-framework-datatest:$version"
        private const val koinExtensionVersion = "1.1.0"
        const val koin = "io.kotest.extensions:kotest-extensions-koin:$koinExtensionVersion"
        private const val ktorAssertionVersion = "1.0.3"
        const val ktor = "io.kotest.extensions:kotest-assertions-ktor:$ktorAssertionVersion"
    }

    object Mockk {
        private const val version = "1.12.2"
        const val core = "io.mockk:mockk:$version"
    }

    object Karate {
        private const val version = "1.1.0"
        const val junit5 = "com.intuit.karate:karate-junit5:$version"
    }
}
