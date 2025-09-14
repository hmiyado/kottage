package com.github.hmiyado.kottage.service.users

object ByteArrayStringifier {
    fun stringify(byteArray: ByteArray): String = byteArray.joinToString("") { "%02x".format(it) }
}
