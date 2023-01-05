package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.OidcToken
import com.github.hmiyado.kottage.model.Salt
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.Password

class UserRepositoryMemory : UserRepository {
    private val list = listOf(User(1, "user1")).toMutableList()
    override fun getUsers(): List<User> {
        return list
    }

    override fun findUserById(id: Long): User? {
        return list.find { it.id == id }
    }

    override fun findUserByScreenName(screenName: String): User? {
        return list.find { it.screenName == screenName }
    }

    override fun findUserByOidc(token: OidcToken): User? {
        return list.firstOrNull()
    }

    override fun getUserWithCredentialsByScreenName(screenName: String): Triple<User, Password, Salt>? {
        return list.find { it.screenName == screenName }?.let { Triple(it, Password("password"), Salt("")) }
    }

    override fun createUser(screenName: String, password: String, salt: String): User {
        val user = User(id = list.last().id + 1, screenName = screenName)
        list.add(user)
        return user
    }

    override fun createUserByOidc(token: OidcToken): User {
        val id = list.last().id + 1
        val user = User(id = list.last().id + 1, screenName = "user$id")
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
