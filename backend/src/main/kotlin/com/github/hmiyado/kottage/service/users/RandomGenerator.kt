package com.github.hmiyado.kottage.service.users

import kotlin.random.Random

class RandomGenerator(
    private val random: Random,
) {
    fun generateString(): String = random.nextBytes(64).let(ByteArrayStringifier::stringify)
}
