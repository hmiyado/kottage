package com.github.hmiyado.kottage.service.users

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.UserRepository
import kotlin.random.Random

interface UsersService {
    fun getUsers(): List<User>

    fun getUser(id: Long): User?

    @Throws(DuplicateScreenNameException::class)
    fun createUser(screenName: String, rawPassword: String): User

    data class DuplicateScreenNameException(
        val screenName: String
    ) : IllegalStateException("screeName \"$screenName\" is duplicated")
}

class UsersServiceImpl(
    private val userRepository: UserRepository,
    private val passwordGenerator: PasswordGenerator,
    private val random: Random = Random.Default
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
        val salt = random.nextBytes(64).joinToString("") { "%02x".format(it) }
        val securePassword = passwordGenerator.generateSecurePassword(rawPassword, salt)
        return userRepository.createUser(screenName, securePassword.value, salt)
    }
}
