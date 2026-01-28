package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.application.configuration.AuthenticationConfiguration
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.UserPasswordCredential
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class AuthenticationAdminTest : DescribeSpec() {
    @MockK
    private lateinit var usersService: UsersService

    @MockK
    private lateinit var adminsService: AdminsService

    @MockK
    private lateinit var authenticationConfig: AuthenticationConfig

    init {
        beforeTest {
            MockKAnnotations.init(this@AuthenticationAdminTest, relaxUnitFun = true)
            clearMocks(usersService, adminsService, authenticationConfig)
        }

        describe("admin initialization") {
            it("should create admin user when user does not exist") {
                val adminName = "admin"
                val adminPassword = "password"
                val credential = UserPasswordCredential(adminName, adminPassword)
                val authConfig = AuthenticationConfiguration(credential)
                val newAdmin = User(id = 1, screenName = adminName)

                // Mock: User does not exist
                every { usersService.getUserByScreenName(adminName) } returns null
                // Mock: Create new user
                every { usersService.createUser(adminName, adminPassword) } returns newAdmin
                // Mock: User is not admin yet
                every { adminsService.isAdmin(newAdmin.id) } returns false
                // Mock: Add admin
                every { adminsService.addAdmin(newAdmin) } just Runs

                authenticationConfig.admin(usersService, adminsService, authConfig)

                verify(exactly = 1) { usersService.getUserByScreenName(adminName) }
                verify(exactly = 1) { usersService.createUser(adminName, adminPassword) }
                verify(exactly = 1) { adminsService.addAdmin(newAdmin) }
            }

            it("should not create admin user when user already exists") {
                val adminName = "admin"
                val adminPassword = "password"
                val credential = UserPasswordCredential(adminName, adminPassword)
                val authConfig = AuthenticationConfiguration(credential)
                val existingAdmin = User(id = 1, screenName = adminName)

                // Mock: User already exists
                every { usersService.getUserByScreenName(adminName) } returns existingAdmin
                // Mock: User is already admin
                every { adminsService.isAdmin(existingAdmin.id) } returns true

                authenticationConfig.admin(usersService, adminsService, authConfig)

                verify(exactly = 1) { usersService.getUserByScreenName(adminName) }
                verify(exactly = 0) { usersService.createUser(any(), any()) }
                verify(exactly = 0) { adminsService.addAdmin(any()) }
            }

            it("should add admin role when user exists but is not admin") {
                val adminName = "admin"
                val adminPassword = "password"
                val credential = UserPasswordCredential(adminName, adminPassword)
                val authConfig = AuthenticationConfiguration(credential)
                val existingUser = User(id = 1, screenName = adminName)

                // Mock: User already exists
                every { usersService.getUserByScreenName(adminName) } returns existingUser
                // Mock: User is not admin yet
                every { adminsService.isAdmin(existingUser.id) } returns false
                // Mock: Add admin
                every { adminsService.addAdmin(existingUser) } just Runs

                authenticationConfig.admin(usersService, adminsService, authConfig)

                verify(exactly = 1) { usersService.getUserByScreenName(adminName) }
                verify(exactly = 0) { usersService.createUser(any(), any()) }
                verify(exactly = 1) { adminsService.addAdmin(existingUser) }
            }

            it("should handle null authentication configuration") {
                // No authentication configuration provided
                authenticationConfig.admin(usersService, adminsService, null)

                // Should not attempt to create or check user
                verify(exactly = 0) { usersService.getUserByScreenName(any()) }
                verify(exactly = 0) { usersService.createUser(any(), any()) }
                verify(exactly = 0) { adminsService.addAdmin(any()) }
            }

            it("should handle duplicate screenName exception during admin creation") {
                val adminName = "admin"
                val adminPassword = "password"
                val credential = UserPasswordCredential(adminName, adminPassword)
                val authConfig = AuthenticationConfiguration(credential)
                val existingAdmin = User(id = 1, screenName = adminName)

                // Mock: First call returns null (user doesn't exist)
                // Mock: createUser throws DuplicateScreenNameException (race condition)
                // Mock: Second getUserByScreenName returns existing user
                every { usersService.getUserByScreenName(adminName) } returnsMany listOf(null, existingAdmin)
                every { usersService.createUser(adminName, adminPassword) } throws
                    UsersService.DuplicateScreenNameException(adminName)
                // Mock: User is not admin yet
                every { adminsService.isAdmin(existingAdmin.id) } returns false
                // Mock: Add admin
                every { adminsService.addAdmin(existingAdmin) } just Runs

                authenticationConfig.admin(usersService, adminsService, authConfig)

                verify(exactly = 2) { usersService.getUserByScreenName(adminName) }
                verify(exactly = 1) { usersService.createUser(adminName, adminPassword) }
                verify(exactly = 1) { adminsService.addAdmin(existingAdmin) }
            }
        }
    }
}
