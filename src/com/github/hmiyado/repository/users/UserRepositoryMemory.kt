package com.github.hmiyado.repository.users

import com.github.hmiyado.model.User

class UserRepositoryMemory : UserRepository {
    private val list = listOf(User(1, "user 1"))
    override fun getUsers(): List<User> {
        return list
    }

    override fun getUser(id: Long): User? {
        return list.find { it.id == id }
    }
}
