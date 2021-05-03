package com.github.hmiyado.model

import com.github.hmiyado.model.serializer.ZonedDateTimeSerializer
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    /**
     * [serialNumber] is unique in all Articles.
     * The [serialNumber] of the article created just after the article with [serialNumber]: 1 is 2.
     */
    val serialNumber: Long = 0,
    val title: String = "No title",
    val body: String = "",
    @Serializable(with = ZonedDateTimeSerializer::class)
    val dateTime: ZonedDateTime = Instant.EPOCH.atZone(ZoneId.systemDefault())
)
