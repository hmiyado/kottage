package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.User

interface UserRepository {
    fun getUsers(): List<User>

    fun getUser(id: Long): User?

    fun createUser(screenName: String, password: String): User
}
