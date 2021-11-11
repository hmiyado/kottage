package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.model.User
import io.ktor.auth.Principal

data class UserPrincipal(
    val user: User
) : Principal
