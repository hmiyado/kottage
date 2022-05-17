package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.kotlinxJson
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.contentType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val kottageJson = Json {
    encodeDefaults = true
    serializersModule = kotlinxJson.serializersModule
}

inline infix fun <reified T> TestApplicationResponse.shouldMatchAsJson(content: T) {
    kottageJson.decodeFromString<T>(this.content!!) shouldBe kottageJson.encodeToString(content)
        .let { kottageJson.decodeFromString(it) }
}

infix fun TestApplicationResponse.shouldHaveStatus(status: HttpStatusCode) {
    status() shouldBe status
}

infix fun TestApplicationResponse.shouldHaveContentType(contentType: ContentType) {
    this.contentType() shouldBe contentType
}

fun TestApplicationResponse.shouldHaveHeader(key: String, value: String) {
    this.headers[key] shouldBe value
}
