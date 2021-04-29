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
}