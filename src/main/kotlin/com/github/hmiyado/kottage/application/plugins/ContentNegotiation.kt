package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.model.serializer.ZonedDateTimeSerializer
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json
import java.time.ZonedDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val kotlinxJson = Json {
    serializersModule = SerializersModule {
        contextual<ZonedDateTime>(ZonedDateTimeSerializer)
    }
}

fun Application.contentNegotiation() {
    install(ContentNegotiation) {
        json(kotlinxJson)
    }
}
