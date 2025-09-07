package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.kotlinxJson
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.testing.TestApplicationResponse
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

val kottageJson = Json {
    encodeDefaults = true
    serializersModule = kotlinxJson.serializersModule
}

inline infix fun <reified T> TestApplicationResponse.shouldMatchAsJson(content: T) {
}

suspend inline infix fun <reified T> HttpResponse.shouldMatchAsJson(content: T) {
    this shouldHaveContentType ContentType.Application.Json.withCharset(Charset.forName("UTF-8"))
    val json = kottageJson.decodeFromString<T>(this.bodyAsText(Charset.defaultCharset()))
    json shouldBe kottageJson.encodeToString(content)
        .let { kottageJson.decodeFromString(it) }
}

infix fun TestApplicationResponse.shouldHaveStatus(status: HttpStatusCode) {
}

infix fun HttpResponse.shouldHaveStatus(status: HttpStatusCode) {
    status shouldBe status
}

infix fun HttpResponse.shouldHaveContentType(contentType: ContentType) {
    this.contentType() shouldBe contentType
}

fun HttpResponse.shouldHaveHeader(key: String, value: String) {
    this.headers[key] shouldBe value
}
