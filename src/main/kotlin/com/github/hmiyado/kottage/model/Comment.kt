package com.github.hmiyado.kottage.model

import java.time.ZonedDateTime

data class Comment(
    val id: Long = 0,
    val body: String = "",
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val author: User = User(),
)
