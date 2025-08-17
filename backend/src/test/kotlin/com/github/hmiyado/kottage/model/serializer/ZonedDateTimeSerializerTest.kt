package com.github.hmiyado.kottage.model.serializer

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ZonedDateTimeSerializerTest : DescribeSpec() {
    init {
        describe("ZonedDateTimeSerializer") {
            it("should encode 2021/05/05 21:12:00 to json") {
                val date = ZonedDateTime.of(2021, 5, 5, 21, 12, 0, 0, ZoneOffset.UTC)
                Json.encodeToString(ZonedDateTimeSerializer, date) shouldEqualJson "\"2021-05-05T21:12:00+0000\""
            }
            it("should encode 2022/01/11 00:26:07 to json ( hour of 0 should be 0 not 24 )") {
                val date = ZonedDateTime.of(2022, 1, 11, 0, 26, 7, 0, ZoneOffset.UTC)
                Json.encodeToString(ZonedDateTimeSerializer, date) shouldEqualJson "\"2022-01-11T00:26:07+0000\""
            }
            it("should decode 2021/05/05 21:12:00 from json") {
                val original = "2021-05-05T21:12:00+0000"
                val expected = ZonedDateTime.of(2021, 5, 5, 21, 12, 0, 0, ZoneOffset.UTC)
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
