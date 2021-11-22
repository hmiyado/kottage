package kottage.users.admins

import com.intuit.karate.junit5.Karate

class AdminsTest {
    @Karate.Test
    fun users(): Karate? {
        return Karate.run("admins").relativeTo(javaClass)
    }
}
