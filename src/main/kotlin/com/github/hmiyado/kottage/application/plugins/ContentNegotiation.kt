package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.model.serializer.ZonedDateTimeSerializer
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val kotlinxJson = Json {
    serializersModule = SerializersModule {
        contextual(ZonedDateTimeSerializer)
    }
}

fun Application.contentNegotiation() {
    install(ContentNegotiation) {
        json(json = kotlinxJson, contentType = ContentType.Application.Json)
    }
}
