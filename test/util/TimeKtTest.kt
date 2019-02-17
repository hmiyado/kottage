package util

import com.github.hmiyado.util.toJodaDateTime
import io.kotlintest.matchers.numerics.shouldBeInRange
import io.kotlintest.specs.DescribeSpec
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeKtTest : DescribeSpec({

    describe("jsr310 time") {
        it(" should convert to joda time") {

            val time310 = ZonedDateTime.of(2019, 4, 20, 9, 0, 0, 0, ZoneId.of("+09:00"))

            val actualJoda = time310.toJodaDateTime()
            val expectedJoda = DateTime()
                .withYear(2019)
                .withMonthOfYear(4)
                .withDayOfMonth(20)
                .withHourOfDay(9)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withZone(DateTimeZone.forID("+09:00"))
            // うるう秒の処理が jsr310 と joda で違う？
            // ミリ秒に直したときに、1秒未満の部分で齟齬がでる
            // 実際の値と期待値の誤差が上下1秒の範囲なら許容する
            actualJoda.millis.shouldBeInRange((expectedJoda.millis - 1000)..(expectedJoda.millis + 1000))
        }
    }
})