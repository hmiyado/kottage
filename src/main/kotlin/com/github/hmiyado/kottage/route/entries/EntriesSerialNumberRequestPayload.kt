package com.github.hmiyado.kottage.route.entries

import kotlinx.serialization.Serializable

class EntriesSerialNumberRequestPayload {
    @Serializable
    data class Patch(
        val title: String? = null,
        val body: String? = null
    )
}
