package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.delete
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.patch
import com.github.hmiyado.kottage.helper.routing
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.serialization.json.put

class UsersIdLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    private lateinit var service: UsersService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this)
        routing {
            UsersIdLocation(service).addRoute(this)
        }

        describe("GET ${Paths.usersIdGet}") {
            it("should return User") {
                val expected = User(id = 1)
                every { service.getUser(1) } returns expected
                get(Paths.usersIdGet.assignPathParams(1)) {
                    authorizeAsAdmin(User())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return BadRequest") {
                get(Paths.usersIdGet.assignPathParams("string")) {
                    authorizeAsAdmin(User())
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Unauthorized") {
                get(Paths.usersIdGet.assignPathParams(1)).run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }

            it("should return NotFound") {
                every { service.getUser(1) } returns null
                get(Paths.usersIdGet.assignPathParams(1)) {
                    authorizeAsAdmin(User())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("PATCH ${Paths.usersIdPatch}") {
            it("should update User") {
                val expected = User(id = 1, screenName = "updated_user")
                every { service.updateUser(1, "updated_user") } returns expected
                patch(
                    Paths.usersIdPatch.assignPathParams("id" to expected.id),
                    { put("screenName", expected.screenName) },
                ) {
                    authorizeAsUser(expected)
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return BadRequest when request body is empty") {
                patch(Paths.usersIdPatch.assignPathParams("id" to 1), "") {
                    authorizeAsUser(User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Forbidden when session user does not match to path user") {
                patch(Paths.usersIdPatch.assignPathParams("id" to 1), { put("screenName", "name") }) {
                    authorizeAsUser(User(id = 2))
                }.run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }

            it("should return NotFound when update user is not found") {
                every { service.updateUser(1, "name") } returns null
                patch(Paths.usersIdPatch.assignPathParams("id" to 1), { put("screenName", "name") }) {
                    authorizeAsUser(User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE ${Paths.usersIdDelete}") {
            it("should delete User") {
                every { service.deleteUser(1) } just Runs
                delete(Paths.usersIdDelete.assignPathParams("id" to 1)) {
                    authorizeAsUser(User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return Forbidden when session user does not match to path user") {
                every { service.deleteUser(1) } just Runs
                delete(Paths.usersIdDelete.assignPathParams("id" to 1)) {
                    authorizeAsUser(User(id = 2))
                }.run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
        }
    }

}
