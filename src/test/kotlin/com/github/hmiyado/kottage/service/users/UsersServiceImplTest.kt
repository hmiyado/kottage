package com.github.hmiyado.kottage.service.users

import com.github.hmiyado.kottage.model.Salt
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class UsersServiceImplTest : DescribeSpec() {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordGenerator: PasswordGenerator

    @MockK
    private lateinit var saltGenerator: SaltGenerator
    private lateinit var service: UsersService

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this)
        service = UsersServiceImpl(userRepository, passwordGenerator, saltGenerator)
    }

    init {
        describe("getUsers") {
            it("should return Users") {
                val expected = listOf(User(id = 1))
                every { userRepository.getUsers() } returns expected
                val actual = service.getUsers()
                actual shouldBe expected
            }
        }

        describe("getUser") {
            it("should return User") {
                val expected = User(id = 1)
                every { userRepository.getUser(1) } returns expected
                val actual = service.getUser(1)
                actual shouldBe expected
            }
            it("should return null") {
                every { userRepository.getUser(any()) } returns null
                val actual = service.getUser(1)
                actual shouldBe null
            }
        }

        describe("createUser") {
            it("should create User") {
                val expected = User(id = 1, "firstUser")
                every { userRepository.getUsers() } returns emptyList()
                every { saltGenerator.generateSalt() } returns "salt"
                every {
                    passwordGenerator.generateSecurePassword("password", any())
                } returns Password("secured password")
                every { userRepository.createUser("firstUser", "secured password", any()) } returns expected
                val actual = service.createUser("firstUser", "password")
                actual shouldBe expected
            }
            it("should not return User when screen name is duplicate") {
                every { userRepository.getUsers() } returns listOf(User(id = 1, screenName = "firstUser"))
                shouldThrow<UsersService.DuplicateScreenNameException> {
                    service.createUser("firstUser", "password")
                }
            }
            it("should generate two salts for two creation") {
                val salts = listOf("salt1", "salt2")
                every { userRepository.getUsers() } returns emptyList()
                every { saltGenerator.generateSalt() } returnsMany salts
                every { passwordGenerator.generateSecurePassword(any(), any()) } returns Password("secure password")
                every { userRepository.createUser(any(), any(), any()) } answers {
                    User(id = invocation.timestamp, screenName = firstArg() as String)
                }
                service.createUser("user", "password")
                verify {
                    userRepository.createUser(
                        "user",
                        "secure password",
                        salts.first()
                    )
                }
                service.createUser("user", "password")
                verify {
                    userRepository.createUser(
                        "user",
                        "secure password",
                        salts[1]
                    )
                }
            }
        }

        describe("updateUser") {
            it("should update User") {
                val expected = User(id = 1, screenName = "user")
                every { userRepository.updateUser(1, "updated User") } returns expected
                val actual = service.updateUser(1, "updated User")
                actual shouldBe expected
            }
        }

        describe("authenticateUser") {
            it("should return User") {
                val expected = User(id = 1)
                val password = Password("secure password")
                val salt = Salt("salt")
                every { userRepository.getUserWithCredentialsByScreenName(expected.screenName) } returns Triple(
                    expected,
                    password,
                    salt
                )
                every { passwordGenerator.generateSecurePassword(any(), any()) } returns Password("secure password")
                val actual = service.authenticateUser(expected.screenName, password.value)
                actual shouldBe expected
            }
            it("should return null when no user matches its screen name") {
                every { userRepository.getUserWithCredentialsByScreenName(any()) } returns null
                val actual = service.authenticateUser("screenName", "password")
                actual shouldBe null
            }
            it("should return null when no user matches its password") {
                val expected = User(id = 1)
                val password = Password("password")
                val salt = Salt("salt")
                every { userRepository.getUserWithCredentialsByScreenName(any()) } returns Triple(
                    expected,
                    password,
                    salt
                )
                every { passwordGenerator.generateSecurePassword(any(), any()) } returns Password("secure password")
                val actual = service.authenticateUser("screenName", "not-matched-password")
                actual shouldBe null
            }
        }


        describe("deleteUser") {
            it("should delete User") {
                every { userRepository.deleteUser(1) } just Runs
                service.deleteUser(1)
                verify { userRepository.deleteUser(1) }
            }
        }
    }

}
