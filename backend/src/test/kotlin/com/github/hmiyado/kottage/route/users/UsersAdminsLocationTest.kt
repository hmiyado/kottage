package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.models.Admin
import com.github.hmiyado.kottage.openapi.models.AdminsGetResponse
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest

class UsersAdminsLocationTest : DescribeSpec(), KoinTest {
    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    private lateinit var adminsService: AdminsService

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@UsersAdminsLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)
    }

    private val init: ApplicationTestBuilder.() -> Unit = {
        authorizationHelper.installSessionAuthentication(this)
        application {
            routing {
                UsersAdminsLocation(usersService, adminsService).addRoute(this)
            }
        }
    }

    init {

        describe("GET ${Paths.usersAdminsGet}") {
            it("should return admins") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    val adminId = admin.id
                    val users = (1L..10).map { User(id = it) }
                    every { usersService.getUsers() } returns users
                    every { adminsService.isAdmin(adminId) } returns true
                    every { adminsService.isAdmin(not(adminId)) } returns false
                    
                    val response = client.get(Paths.usersAdminsGet) {
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson AdminsGetResponse(listOf(Admin(adminId)))
                }
            }
        }

        describe("PATCH ${Paths.usersAdminsPatch}") {
            it("should add admin when target is not admin") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    val adminId = admin.id
                    val target = User(id = 1, screenName = "updated_user")
                    every { usersService.getUser(target.id) } returns target
                    every { adminsService.isAdmin(adminId) } returns false
                    every { adminsService.addAdmin(target) } just Runs
                    
                    val response = client.patch(Paths.usersAdminsPatch) {
                        setBody(buildJsonObject {
                            put("id", target.id)
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { adminsService.addAdmin(target) }
                }
            }

            it("should not add admin when target is admin") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    val adminId = admin.id
                    val target = User(id = 1, screenName = "updated_user")
                    every { usersService.getUser(target.id) } returns target
                    every { adminsService.isAdmin(adminId) } returns true
                    every { adminsService.isAdmin(target.id) } returns false
                    
                    val response = client.patch(Paths.usersAdminsPatch) {
                        setBody(buildJsonObject {
                            put("id", target.id)
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return BadRequest when request body is empty") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    
                    val response = client.patch(Paths.usersAdminsPatch) {
                        setBody("")
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return NotFound when update user is not found") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    every { usersService.getUser(any<Long>()) } returns null
                    
                    val response = client.patch(Paths.usersAdminsPatch) {
                        setBody(buildJsonObject {
                            put("id", "999")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE ${Paths.usersAdminsDelete}") {
            it("should remove from Admin") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    val target = User(id = 1, screenName = "updated_user")
                    every { usersService.getUser(target.id) } returns target
                    every { adminsService.isAdmin(target.id) } returns true
                    every { adminsService.removeAdmin(target) } just Runs
                    
                    val response = client.delete(Paths.usersAdminsDelete) {
                        setBody(buildJsonObject {
                            put("id", target.id)
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { adminsService.removeAdmin(target) }
                }
            }

            it("should not remove from Admin when target is not admin") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    val target = User(id = 1, screenName = "updated_user")
                    every { usersService.getUser(target.id) } returns target
                    every { adminsService.isAdmin(target.id) } returns false
                    every { adminsService.isAdmin(target.id) } returns false
                    every { adminsService.removeAdmin(target) } just Runs
                    
                    val response = client.delete(Paths.usersAdminsDelete) {
                        setBody(buildJsonObject {
                            put("id", target.id)
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return BadRequest when request body is empty") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    
                    val response = client.delete(Paths.usersAdminsDelete) {
                        setBody("")
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return NotFound when update user is not found") {
                testApplication {
                    init()
                    val admin = User(id = 5)
                    every { usersService.getUser(any<Long>()) } returns null
                    
                    val response = client.delete(Paths.usersAdminsDelete) {
                        setBody(buildJsonObject {
                            put("id", "999")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
