package com.github.hmiyado.kottage.model

import com.github.hmiyado.kottage.route.users.UsersLocation
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import com.github.hmiyado.kottage.openapi.models.Entry as OpenApiEntry

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
    val commentsTotalCount: Long = 0,
    val author: User = User(),
)

fun Entry.toEntryResponse(): OpenApiEntry =
    OpenApiEntry(
        serialNumber = serialNumber,
        title = title,
        body = body,
        dateTime = dateTime,
        commentsTotalCount = commentsTotalCount,
        author = with(UsersLocation) { author.toResponseUser() },
    )
