package com.github.hmiyado.util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.ZonedDateTime

/**
 * JSR310 の [ZonedDateTime] を Joda 形式の [DateTime] に変換する。
 */
fun ZonedDateTime.toJodaDateTime(): DateTime = DateTime()
    .withMillis(toInstant().toEpochMilli())
    .withZone(DateTimeZone.forID(zone.id))

