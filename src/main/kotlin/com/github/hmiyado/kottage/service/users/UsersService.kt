package com.github.hmiyado.kottage.service.users

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.UserRepository

interface UsersService {
    fun getUsers(): List<User>

    fun getUser(id: Long): User?

    @Throws(DuplicateScreenNameException::class)
    fun createUser(screenName: String, rawPassword: String): User

    fun updateUser(id: Long, screenName: String?): User?

    fun deleteUser(id: Long)

    data class DuplicateScreenNameException(
        val screenName: String
    ) : IllegalStateException("screeName \"$screenName\" is duplicated")
}

class UsersServiceImpl(
    private val userRepository: UserRepository,
    private val passwordGenerator: PasswordGenerator,
    private val saltGenerator: SaltGenerator,
) : UsersService {
    override fun getUsers(): List<User> {
        return userRepository.getUsers()
    }

    override fun getUser(id: Long): User? {
        return userRepository.getUser(id)
    }

    override fun createUser(screenName: String, rawPassword: String): User {
        val isScreenNameDuplicated = getUsers().any { it.screenName == screenName }
        if (isScreenNameDuplicated) {
            throw UsersService.DuplicateScreenNameException(screenName)
        }
        val salt = saltGenerator.generateSalt()
        val securePassword = passwordGenerator.generateSecurePassword(rawPassword, salt)
        return userRepository.createUser(screenName, securePassword.value, salt)
    }

    override fun updateUser(id: Long, screenName: String?): User? {
        return userRepository.updateUser(id, screenName)
    }

    override fun deleteUser(id: Long) {
        userRepository.deleteUser(id)
    }
}
