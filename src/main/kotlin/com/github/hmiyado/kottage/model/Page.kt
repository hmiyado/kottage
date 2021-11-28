package com.github.hmiyado.kottage.model

data class Page<T : Any>(
    val totalCount: Long = 0,
    val items: List<T> = emptyList(),
    val limit: Long = 0,
    val offset: Long = 0,
)
