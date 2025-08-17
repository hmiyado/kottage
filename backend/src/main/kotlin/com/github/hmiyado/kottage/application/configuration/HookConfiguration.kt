package com.github.hmiyado.kottage.application.configuration

import io.ktor.http.HttpMethod

data class HookConfiguration(
    val name: String,
    val method: HttpMethod,
    val path: String,
    val requestTo: String,
)
