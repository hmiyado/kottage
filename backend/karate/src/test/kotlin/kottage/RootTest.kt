package kottage

import com.intuit.karate.junit5.Karate


class RootTest {
    @Karate.Test
    fun rootGet(): Karate? {
        return Karate.run("root").relativeTo(javaClass)
    }
}
