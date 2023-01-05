package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.OidcToken
import com.github.hmiyado.kottage.model.Salt
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.Password

interface UserRepository {
    fun getUsers(): List<User>

    fun findUserById(id: Long): User?

    fun findUserByScreenName(screenName: String): User?

    fun getUserWithCredentialsByScreenName(screenName: String): Triple<User, Password, Salt>?

    fun createUser(screenName: String, password: String, salt: String): User

    fun createUserByOidc(token: OidcToken): User

    fun updateUser(id: Long, screenName: String?): User?

    fun deleteUser(id: Long)
}
