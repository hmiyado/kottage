package com.github.hmiyado.util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * JSR310 の [ZonedDateTime] を Joda 形式の [DateTime] に変換する。
 */
fun ZonedDateTime.toJodaDateTime(): DateTime = DateTime()
    .withMillis(toInstant().toEpochMilli())
    .withZone(DateTimeZone.forID(zone.id))

/**
 * Joda 形式の [DateTime] を JSR310 の [ZonedDateTime] に変換する
 */
fun DateTime.toZonedDateTime(): ZonedDateTime = ZonedDateTime.of(
    year,
    monthOfYear,
    dayOfMonth,
    hourOfDay,
    minuteOfHour,
    secondOfMinute,
    0,
    ZoneId.of(zone.id)
)