package com.github.hmiyado.kottage.route.users

import kotlinx.serialization.Serializable

class UsersRequestPayload {
    @Serializable
    data class Post(
        val screenName: String,
        val password: String
    )
}
