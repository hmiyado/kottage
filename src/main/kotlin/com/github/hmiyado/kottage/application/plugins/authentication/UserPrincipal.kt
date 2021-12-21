package com.github.hmiyado.kottage.application.plugins.authentication

import io.ktor.auth.Principal
import com.github.hmiyado.kottage.model.User as UserModel

sealed class UserPrincipal(
    open val user: UserModel
) : Principal {
    open operator fun component1(): UserModel {
        return user
    }

    data class User(
        override val user: UserModel
    ) : UserPrincipal(user)

    data class Admin(
        override val user: UserModel
    ) : UserPrincipal(user)
}
