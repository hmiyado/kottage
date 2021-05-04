package com.github.hmiyado.helper

import io.kotest.matchers.shouldBe
import io.ktor.server.testing.TestApplicationResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline infix fun <reified T> TestApplicationResponse.shouldMatchAsJson(content: T) {
    val json = Json {
        encodeDefaults = true
    }
    json.decodeFromString<T>(this.content!!) shouldBe content
}
