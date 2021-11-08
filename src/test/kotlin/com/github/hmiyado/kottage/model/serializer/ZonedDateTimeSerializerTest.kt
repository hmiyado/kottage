package com.github.hmiyado.kottage.model.serializer

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class ZonedDateTimeSerializerTest : DescribeSpec() {
    init {
        describe("ZonedDateTimeSerializer") {
            it("should encode 2021/05/05 21:12:00 Asia/Tokyo to json") {
                val date = ZonedDateTime.of(2021, 5, 5, 21, 12, 0, 0, ZoneId.of("Asia/Tokyo"))
                Json.encodeToString(ZonedDateTimeSerializer, date) shouldEqualJson "\"2021-05-05T21:12:00+0900\""
            }
            it("should decode 2021/05/05 21:12:00 Asia/Tokyo from json") {
                val original = "2021-05-05T21:12:00+0900"
                val expected = ZonedDateTime.of(2021, 5, 5, 21, 12, 0, 0, ZoneOffset.ofHours(9))
                Json.decodeFromJsonElement(ZonedDateTimeSerializer, JsonPrimitive(original)) shouldBe expected
            }
            it("should be equal original and value decoded and encoded") {
                val original = "2021-05-05T21:12:00+0900"
                val decoded = Json.decodeFromJsonElement(ZonedDateTimeSerializer, JsonPrimitive(original))
                val decodedAndEncoded = Json.encodeToString(ZonedDateTimeSerializer, decoded)
                decodedAndEncoded shouldBe JsonPrimitive(original).toString()
            }
        }
    }
}
