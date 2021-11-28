package com.github.hmiyado.kottage.model

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlinx.serialization.Contextual
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
    @Contextual
    val dateTime: ZonedDateTime = Instant.EPOCH.atZone(ZoneOffset.UTC),
    val author: User = User(),
)
