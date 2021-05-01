package com.github.hmiyado

object Dependencies {
    object Logback {
        const val classic = "ch.qos.logback:logback-classic:1.2.1"
    }

    object Ktor {
        private const val version = "1.5.3"
        const val serverNetty = "io.ktor:ktor-server-netty:$version"
        const val serialization = "io.ktor:ktor-serialization:$version"
        const val test = "io.ktor:ktor-server-tests:$version"
    }

    object Exposed {
        private const val version = "0.31.1"
        const val core = "org.jetbrains.exposed:exposed-core:$version"
        const val dao = "org.jetbrains.exposed:exposed-dao:$version"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc:$version"
        const val javaTime = "org.jetbrains.exposed:exposed-java-time:$version"
    }

    object Koin {
        private const val version = "3.0.1"
        const val ktor = "io.insert-koin:koin-ktor:$version"
        const val test = "io.insert-koin:koin-test:$version"
    }

    object PostgreSql {
        private const val version = "42.2.2"
        const val core = "org.postgresql:postgresql:$version"
    }

    object JUnit {
        private const val version = "5.4.0"
        const val jupiter = "org.junit.jupiter:junit-jupiter-api:5.4.0"
    }

    object Kotest {
        private const val version = "4.4.3"
        const val jUnit5 = "io.kotest:kotest-runner-junit5:$version"
        const val json = "io.kotest:kotest-assertions-json:$version"
        const val ktor = "io.kotest:kotest-assertions-ktor:$version"
        private const val koinExtensionVersion = "1.0.0"
        const val koin = "io.kotest.extensions:kotest-extensions-koin:$koinExtensionVersion"
    }
}
