package kottage.health

import com.intuit.karate.junit5.Karate

class HealthTest {
    @Karate.Test
    fun health(): Karate? {
        return Karate.run("health").relativeTo(javaClass)
    }
}
