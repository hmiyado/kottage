package com.github.hmiyado.kottage.service.users

import kotlin.random.Random

class SaltGenerator(
    private val random: Random
) {
    fun generateSalt(): String {
        return random.nextBytes(64).joinToString("") { "%02x".format(it) }
    }
}
