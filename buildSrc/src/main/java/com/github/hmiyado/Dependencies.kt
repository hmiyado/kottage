package com.github.hmiyado

interface Dependencies {
    val implementations: List<String>
    val testImplementations: List<String>

    object Kottage {
        private val dependencies = listOf<Dependencies>(
        )
        val implementations = dependencies.flatMap { it.implementations }
        val testImplementations = dependencies.flatMap { it.testImplementations }
    }

    object Kotlin {
        const val jvmTarget = "11"
    }
}
