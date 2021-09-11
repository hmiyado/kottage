package com.github.hmiyado.kottage.route.entries

import kotlinx.serialization.Serializable

class EntriesRequestPayload {
    @Serializable
    data class Post(
        val title: String,
        val body: String
    )
}
