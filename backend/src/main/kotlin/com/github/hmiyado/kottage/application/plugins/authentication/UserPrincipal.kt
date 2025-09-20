package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.model.User as UserModel

sealed class UserPrincipal(
    open val user: UserModel,
) {
    open operator fun component1(): UserModel = user

    data class User(
        override val user: UserModel,
    ) : UserPrincipal(user)

    data class Admin(
        override val user: UserModel,
    ) : UserPrincipal(user)
}
