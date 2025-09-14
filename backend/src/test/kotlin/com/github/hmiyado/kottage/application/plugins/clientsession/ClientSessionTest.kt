package com.github.hmiyado.kottage.application.plugins.clientsession

import kotlinx.serialization.json.Json

fun ClientSession.toJsonString(): String = Json.encodeToString(this)
