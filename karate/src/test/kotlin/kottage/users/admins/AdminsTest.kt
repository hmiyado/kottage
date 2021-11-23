package kottage.users.admins

import com.intuit.karate.junit5.Karate

class AdminsTest {
    @Karate.Test
    fun admins(): Karate? {
        return Karate.run("admins").relativeTo(javaClass)
    }
}
