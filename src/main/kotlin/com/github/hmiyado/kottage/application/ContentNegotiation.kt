package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.model.serializer.ZonedDateTimeSerializer
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

fun Application.contentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(ZonedDateTimeSerializer)
            }
        })
    }
}
