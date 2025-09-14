package com.github.hmiyado.kottage.model

import kotlinx.serialization.json.Json

fun UserSession.toJsonString() = Json.encodeToString(this)
