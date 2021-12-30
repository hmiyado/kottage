package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.models.Admin
import com.github.hmiyado.kottage.openapi.models.AdminsGetResponse
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.setBody
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UsersAdminsLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@UsersAdminsLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)

        RoutingTestHelper.setupRouting(application) {
            authorizationHelper.installSessionAuthentication(application)
            UsersAdminsLocation(usersService, adminsService).addRoute(this)
        }
    }, afterSpec = {
        clearAllMocks()
    })

    lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    private lateinit var usersService: UsersService

    @MockK
    private lateinit var adminsService: AdminsService

    @MockK
    private lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET ${Paths.usersAdminsGet}") {
            it("should return admins") {
                val admin = User(id = 5)
                val adminId = admin.id
                val users = (1L..10).map { User(id = it) }
                every { usersService.getUsers() } returns users
                every { adminsService.isAdmin(adminId) } returns true
                every { adminsService.isAdmin(not(adminId)) } returns false
                ktorListener.handleJsonRequest(HttpMethod.Get, Paths.usersAdminsGet) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson AdminsGetResponse(listOf(Admin(adminId)))
                }
            }
        }

        describe("PATCH ${Paths.usersAdminsPatch}") {
            it("should add admin when target is not admin") {
                val admin = User(id = 5)
                val adminId = admin.id
                val target = User(id = 1, screenName = "updated_user")
                every { usersService.getUser(target.id) } returns target
                every { adminsService.isAdmin(adminId) } returns false
                every { adminsService.addAdmin(target) } just Runs
                ktorListener.handleJsonRequest(
                    HttpMethod.Patch,
                    Paths.usersAdminsPatch
                ) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody(buildJsonObject {
                        put("id", target.id)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { adminsService.addAdmin(target) }
                }
            }

            it("should not add admin when target is admin") {
                val admin = User(id = 5)
                val adminId = admin.id
                val target = User(id = 1, screenName = "updated_user")
                every { usersService.getUser(target.id) } returns target
                every { adminsService.isAdmin(adminId) } returns true
                every { adminsService.isAdmin(target.id) } returns false
                ktorListener.handleJsonRequest(
                    HttpMethod.Patch,
                    Paths.usersAdminsPatch
                ) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody(buildJsonObject {
                        put("id", target.id)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return BadRequest when request body is empty") {
                val admin = User(id = 5)
                ktorListener.handleJsonRequest(HttpMethod.Patch, Paths.usersAdminsPatch) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return NotFound when update user is not found") {
                val admin = User(id = 5)
                every { usersService.getUser(any()) } returns null
                ktorListener.handleJsonRequest(HttpMethod.Patch, Paths.usersAdminsPatch) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody(buildJsonObject {
                        put("id", "999")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE ${Paths.usersIdPatch}") {
            it("should remove from Admin") {
                val admin = User(id = 5)
                val target = User(id = 1, screenName = "updated_user")
                every { usersService.getUser(target.id) } returns target
                every { adminsService.isAdmin(target.id) } returns true
                every { adminsService.removeAdmin(target) } just Runs
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.usersAdminsPatch
                ) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody(buildJsonObject {
                        put("id", target.id)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    verify { adminsService.removeAdmin(target) }
                }
            }

            it("should not remove from Admin when target is not admin") {
                val admin = User(id = 5)
                val target = User(id = 1, screenName = "updated_user")
                every { usersService.getUser(target.id) } returns target
                every { adminsService.isAdmin(target.id) } returns false
                every { adminsService.isAdmin(target.id) } returns false
                every { adminsService.removeAdmin(target) } just Runs
                ktorListener.handleJsonRequest(
                    HttpMethod.Delete,
                    Paths.usersAdminsPatch
                ) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody(buildJsonObject {
                        put("id", target.id)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return BadRequest when request body is empty") {
                val admin = User(id = 5)
                ktorListener.handleJsonRequest(HttpMethod.Delete, Paths.usersAdminsPatch) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return NotFound when update user is not found") {
                val admin = User(id = 5)
                every { usersService.getUser(any()) } returns null
                ktorListener.handleJsonRequest(HttpMethod.Delete, Paths.usersAdminsPatch) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        admin
                    )
                    setBody(buildJsonObject {
                        put("id", "999")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
