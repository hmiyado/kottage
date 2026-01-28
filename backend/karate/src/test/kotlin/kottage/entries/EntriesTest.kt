package kottage.entries

import com.intuit.karate.junit5.Karate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class EntriesTest {
    @Karate.Test
    fun entries(): Karate? {
        val current = ZonedDateTime.now(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssZ")
        val allowedStartEntryTime = current.format(formatter)
        val allowedEndEntryTime = current.plusMinutes(3).format(formatter)

        return Karate
            .run("entries")
            .systemProperty("allowedStartEntryTime", allowedStartEntryTime)
            .systemProperty("allowedEndEntryTime", allowedEndEntryTime)
            .relativeTo(javaClass)
    }

    @Karate.Test
    fun entriesPagination(): Karate? =
        Karate
            .run("entries-pagination")
            .relativeTo(javaClass)

    @Karate.Test
    fun comments(): Karate? =
        Karate
            .run("comments.feature")
            .relativeTo(javaClass)
}
