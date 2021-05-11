package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.User

class UserRepositoryMemory : UserRepository {
    private val list = listOf(User(1, "user 1")).toMutableList()
    override fun getUsers(): List<User> {
        return list
    }

    override fun getUser(id: Long): User? {
        return list.find { it.id == id }
    }

    override fun createUser(screenName: String, password: String, salt: String): User {
        val user = User(id = list.last().id + 1, screenName = screenName)
        list.add(user)
        return user
    }

    override fun updateUser(id: Long, screenName: String?): User? {
        return list.flatMap {
            if (it.id != id || screenName == null) {
                listOf(it)
            } else {
                listOf(it.copy(screenName = screenName))
            }
        }.find { it.id == id }
    }

    override fun deleteUser(id: Long) {
        list.flatMap {
            if (it.id == id) {
                emptyList()
            } else {
                listOf(it)
            }
        }
    }
}
