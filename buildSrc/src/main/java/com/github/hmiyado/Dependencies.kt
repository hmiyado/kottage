package com.github.hmiyado

interface Dependencies {
    val implementations: List<String>
    val testImplementations: List<String>

    object Kottage {
        private val dependencies = listOf(
            Mockk
        )
        val implementations = dependencies.flatMap { it.implementations }
        val testImplementations = dependencies.flatMap { it.testImplementations }
    }

    object Kotlin {
        const val jvmTarget = "11"
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
