package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.kotlinxJson
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.TestApplicationResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val kottageJson = Json {
    encodeDefaults = true
    serializersModule = kotlinxJson.serializersModule
}

inline infix fun <reified T> TestApplicationResponse.shouldMatchAsJson(content: T) {
    kottageJson.decodeFromString<T>(this.content!!) shouldBe content
}
