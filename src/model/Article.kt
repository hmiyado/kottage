package com.github.hmiyado.model

import java.time.ZonedDateTime

data class Article(
    val title: String,
    val body: String,
    val dateTime: ZonedDateTime
)