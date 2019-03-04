package util

import com.github.hmiyado.util.toJodaDateTime
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeKtTest : DescribeSpec({

    describe("jsr310 time") {
        it(" should convert to joda time") {

            val time310 = ZonedDateTime.of(2019, 4, 20, 9, 0, 0, 0, ZoneId.of("+09:00"))
            val expectedJoda = DateTime()
                .withZone(DateTimeZone.forID("+09:00"))
                .withYear(2019)
                .withMonthOfYear(4)
                .withDayOfMonth(20)
                .withHourOfDay(9)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)

            val actualJoda = time310.toJodaDateTime()

            actualJoda.year shouldBe expectedJoda.year
            actualJoda.monthOfYear shouldBe expectedJoda.monthOfYear
            actualJoda.dayOfMonth shouldBe expectedJoda.dayOfMonth
            actualJoda.hourOfDay shouldBe expectedJoda.hourOfDay
            actualJoda.minuteOfHour shouldBe expectedJoda.minuteOfHour
            actualJoda.secondOfMinute shouldBe expectedJoda.secondOfMinute
        }
    }
})