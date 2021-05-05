package com.github.hmiyado.kottage.route

import io.ktor.http.HttpMethod
import io.ktor.response.ApplicationResponse
import io.ktor.response.header

fun ApplicationResponse.allowMethods(vararg methods: HttpMethod) {
    header("Allow", methods.joinToString(", ") { it.value })
}
