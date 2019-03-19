package util

import com.github.hmiyado.util.toJodaDateTime
import com.github.hmiyado.util.toZonedDateTime
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeKtTest : DescribeSpec({
    val time310 = ZonedDateTime.of(2019, 4, 20, 9, 0, 0, 0, ZoneId.of("+09:00"))
    val joda = DateTime()
        .withZone(DateTimeZone.forID("+09:00"))
        .withYear(2019)
        .withMonthOfYear(4)
        .withDayOfMonth(20)
        .withHourOfDay(9)
        .withMinuteOfHour(0)
        .withSecondOfMinute(0)

    describe("jsr310 time") {
        it(" should convert to joda time") {

            val expectedJoda = joda
            val actualJoda = time310.toJodaDateTime()

            actualJoda.year shouldBe expectedJoda.year
            actualJoda.monthOfYear shouldBe expectedJoda.monthOfYear
            actualJoda.dayOfMonth shouldBe expectedJoda.dayOfMonth
            actualJoda.hourOfDay shouldBe expectedJoda.hourOfDay
            actualJoda.minuteOfHour shouldBe expectedJoda.minuteOfHour
            actualJoda.secondOfMinute shouldBe expectedJoda.secondOfMinute
        }
    }

    describe("joda time") {
        it("should convert to jsr310 time") {
            val expected310 = time310
            val actual310 = joda.toZonedDateTime()

            actual310.year shouldBe  expected310.year
            actual310.month shouldBe  expected310.month
            actual310.dayOfMonth shouldBe expected310.dayOfMonth
            actual310.hour shouldBe expected310.hour
            actual310.minute shouldBe expected310.minute
            actual310.second shouldBe expected310.second
            actual310.nano shouldBe expected310.nano
        }
    }
})