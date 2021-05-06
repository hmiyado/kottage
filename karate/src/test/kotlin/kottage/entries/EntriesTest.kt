package kottage.entries

import com.intuit.karate.junit5.Karate

class EntriesTest {
    @Karate.Test
    fun entries(): Karate? {
        return Karate.run("entries").relativeTo(javaClass)
    }
}
