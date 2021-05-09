package com.github.hmiyado.kottage.service.users

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.UserRepository

interface UsersService {
    fun getUsers(): List<User>

    fun getUser(id: Long): User?

    fun createUser(screenName: String, rawPassword: String): User

    data class DuplicateScreenNameException(
        val screenName: String
    ) : IllegalStateException("screeName \"$screenName\" is duplicated")
}

class UsersServiceImpl(
    private val userRepository: UserRepository,
    private val passwordGenerator: PasswordGenerator,
) : UsersService {
    override fun getUsers(): List<User> {
        return userRepository.getUsers()
    }

    override fun getUser(id: Long): User? {
        return userRepository.getUser(id)
    }

    @Throws(UsersService.DuplicateScreenNameException::class)
    override fun createUser(screenName: String, rawPassword: String): User {
        val isScreenNameDuplicated = getUsers().any { it.screenName == screenName }
        if (isScreenNameDuplicated) {
            throw UsersService.DuplicateScreenNameException(screenName)
        }
        val salt = "salt"
        val securePassword = passwordGenerator.generateSecurePassword(rawPassword, salt)
        return userRepository.createUser(screenName, securePassword.value, salt)
    }
}
