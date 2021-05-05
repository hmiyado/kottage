package com.github.hmiyado.repository.users

import com.github.hmiyado.model.User

interface UserRepository {
    fun getUser(id: Long): User?
}
