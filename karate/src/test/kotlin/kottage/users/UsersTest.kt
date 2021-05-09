package kottage.users

import com.intuit.karate.junit5.Karate

class UsersTest {
    @Karate.Test
    fun users(): Karate? {
        return Karate.run("users").relativeTo(javaClass)
    }
}
