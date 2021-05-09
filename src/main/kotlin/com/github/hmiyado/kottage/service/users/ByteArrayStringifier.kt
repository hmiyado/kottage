package com.github.hmiyado.kottage.service.users

object ByteArrayStringifier {
    fun stringify(byteArray: ByteArray): String {
        return byteArray.joinToString("") { "%02x".format(it) }
    }
}
