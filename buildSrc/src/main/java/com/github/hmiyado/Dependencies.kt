package com.github.hmiyado

interface Dependencies {
    val implementations: List<String>
    val testImplementations: List<String>

    object Kotlin {
        const val jvmTarget = "11"
    }
}
