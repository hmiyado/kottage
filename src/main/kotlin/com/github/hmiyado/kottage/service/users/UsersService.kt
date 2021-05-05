package com.github.hmiyado.kottage.service.users

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.UserRepository

interface UsersService {
    fun getUsers(): List<User>

    fun getUser(id: Long): User?
}

class UsersServiceImpl(
    private val userRepository: UserRepository
) : UsersService {
    override fun getUsers(): List<User> {
        return userRepository.getUsers()
    }

    override fun getUser(id: Long): User? {
        return userRepository.getUser(id)
    }
}
