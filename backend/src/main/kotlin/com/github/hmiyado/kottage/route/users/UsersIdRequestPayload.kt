package com.github.hmiyado.kottage.route.users

import kotlinx.serialization.Serializable

class UsersIdRequestPayload {
    @Serializable
    data class Patch(
        val screenName: String,
    )
}
