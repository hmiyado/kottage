package com.github.hmiyado.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val screenName: String = "default",
)
