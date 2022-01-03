package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.delete
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.patch
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.models.Admin
import com.github.hmiyado.kottage.openapi.models.AdminsGetResponse
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.serialization.json.put

class UsersAdminsLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    private lateinit var adminsService: AdminsService

    override fun listeners(): List<TestListener> = listOf(listener)

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        clearAllMocks()
    }

    init {
        MockKAnnotations.init(this)
        routing {
            UsersAdminsLocation(usersService, adminsService).addRoute(this)
        }

        describe("GET ${Paths.usersAdminsGet}") {
            it("should return admins") {
                val admin = User(id = 5)
                val adminId = admin.id
                val users = (1L..10).map { User(id = it) }
                every { usersService.getUsers() } returns users
                every { adminsService.isAdmin(adminId) } returns true
                every { adminsService.isAdmin(not(adminId)) } returns false
                get(Paths.usersAdminsGet) {
                    authorizeAsAdmin(admin)
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
                patch(Paths.usersAdminsPatch, {
                    put("id", target.id)
                }) {
                    authorizeAsAdmin(admin)
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
                patch(Paths.usersAdminsPatch, {
                    put("id", target.id)
                }) {
                    authorizeAsAdmin(admin)
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return BadRequest when request body is empty") {
                val admin = User(id = 5)
                patch(Paths.usersAdminsPatch, "") {
                    authorizeAsAdmin(admin)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return NotFound when update user is not found") {
                val admin = User(id = 5)
                every { usersService.getUser(any()) } returns null
                patch(Paths.usersAdminsPatch, {
                    put("id", "999")
                }) {
                    authorizeAsAdmin(admin)
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE ${Paths.usersAdminsDelete}") {
            it("should remove from Admin") {
                val admin = User(id = 5)
                val target = User(id = 1, screenName = "updated_user")
                every { usersService.getUser(target.id) } returns target
                every { adminsService.isAdmin(target.id) } returns true
                every { adminsService.removeAdmin(target) } just Runs
                delete(Paths.usersAdminsDelete, {
                    put("id", target.id)
                }) {
                    authorizeAsAdmin(admin)
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
                delete(Paths.usersAdminsDelete, {
                    put("id", target.id)
                }) {
                    authorizeAsAdmin(admin)
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return BadRequest when request body is empty") {
                val admin = User(id = 5)
                delete(Paths.usersAdminsDelete, "") {
                    authorizeAsAdmin(admin)
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return NotFound when update user is not found") {
                val admin = User(id = 5)
                every { usersService.getUser(any()) } returns null
                delete(Paths.usersAdminsDelete, {
                    put("id", "999")
                }) {
                    authorizeAsAdmin(admin)
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }
    }
}
