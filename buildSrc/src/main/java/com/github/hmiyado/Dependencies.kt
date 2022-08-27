package com.github.hmiyado

interface Dependencies {
    val implementations: List<String>
    val testImplementations: List<String>

    object Kottage {
        private val dependencies = listOf(
            JUnit, Kotest, Mockk
        )
        val implementations = dependencies.flatMap { it.implementations }
        val testImplementations = dependencies.flatMap { it.testImplementations }
    }

    object Kotlin {
        const val jvmTarget = "11"
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
