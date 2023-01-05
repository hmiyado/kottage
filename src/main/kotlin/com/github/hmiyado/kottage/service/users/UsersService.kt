package com.github.hmiyado.kottage.service.users

import com.github.hmiyado.kottage.model.OidcToken
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.UserRepository

interface UsersService {
    fun getUsers(): List<User>

    fun getUser(id: Long): User?

    @Throws(DuplicateScreenNameException::class)
    fun createUser(screenName: String, rawPassword: String): User

    fun createUserByOidc(token: OidcToken): User

    fun updateUser(id: Long, screenName: String?): User?

    fun authenticateUser(screenName: String, rawPassword: String): User?

    fun deleteUser(id: Long)

    data class DuplicateScreenNameException(
        val screenName: String,
    ) : IllegalStateException("screeName \"$screenName\" is duplicated")
}

class UsersServiceImpl(
    private val userRepository: UserRepository,
    private val passwordGenerator: PasswordGenerator,
    private val randomGenerator: RandomGenerator,
) : UsersService {
    private fun isScreenNameDuplicated(screenName: String): Boolean {
        return userRepository.findUserByScreenName(screenName) != null
    }

    override fun getUsers(): List<User> {
        return userRepository.getUsers()
    }

    override fun getUser(id: Long): User? {
        return userRepository.findUserById(id)
    }

    override fun createUser(screenName: String, rawPassword: String): User {
        if (isScreenNameDuplicated(screenName)) {
            throw UsersService.DuplicateScreenNameException(screenName)
        }
        val salt = randomGenerator.generateString()
        val securePassword = passwordGenerator.generateSecurePassword(rawPassword, salt)
        return userRepository.createUser(screenName, securePassword.value, salt)
    }

    override fun createUserByOidc(token: OidcToken): User {
        return userRepository.createUserByOidc(token)
    }

    override fun updateUser(id: Long, screenName: String?): User? {
        if (screenName != null && isScreenNameDuplicated(screenName)) {
            throw UsersService.DuplicateScreenNameException(screenName)
        }
        return userRepository.updateUser(id, screenName)
    }

    override fun authenticateUser(screenName: String, rawPassword: String): User? {
        val (user, expectedSecurePassword, salt) = userRepository.getUserWithCredentialsByScreenName(screenName)
            ?: return null

        val actualPassword = passwordGenerator.generateSecurePassword(rawPassword, salt.value)
        return if (actualPassword == expectedSecurePassword) {
            user
        } else {
            null
        }
    }

    override fun deleteUser(id: Long) {
        userRepository.deleteUser(id)
    }
}
