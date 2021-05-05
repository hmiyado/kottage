package com.github.hmiyado.kottage.model

import com.github.hmiyado.kottage.model.serializer.ZonedDateTimeSerializer
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    /**
     * [serialNumber] is unique in all Entries.
     * The [serialNumber] of the entry created just after the entry with [serialNumber]: 1 is 2.
     */
    val serialNumber: Long = 0,
    val title: String = "No title",
    val body: String = "",
    @Serializable(with = ZonedDateTimeSerializer::class)
    val dateTime: ZonedDateTime = Instant.EPOCH.atZone(ZoneId.of("Asia/Tokyo"))
)
