package com.github.hmiyado

object Dependencies {
    object Logback {
        const val classic = "ch.qos.logback:logback-classic:1.2.1"
    }

    object Ktor {
        private const val version = "1.5.3"
        const val serverNetty = "io.ktor:ktor-server-netty:$version"
        const val gson = "io.ktor:ktor-gson:$version"
        const val test = "io.ktor:ktor-server-tests:$version"
    }

    object Exposed {
        private const val version = "0.31.1"
        const val core = "org.jetbrains.exposed:exposed-core:$version"
        const val dao = "org.jetbrains.exposed:exposed-dao:$version"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc:$version"
        const val javaTime = "org.jetbrains.exposed:exposed-java-time:$version"
    }
}