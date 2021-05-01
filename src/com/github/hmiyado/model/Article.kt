package com.github.hmiyado.model

import com.github.hmiyado.model.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val title: String,
    val body: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val dateTime: ZonedDateTime
)
