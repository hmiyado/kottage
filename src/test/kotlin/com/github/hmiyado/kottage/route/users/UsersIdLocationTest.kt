package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.setBody
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@KtorExperimentalLocationsAPI
class UsersIdLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@UsersIdLocationTest)
        authorizationHelper = AuthorizationHelper(service, sessionStorage)

        RoutingTestHelper.setupRouting(application) {
            authorizationHelper.installSessionAuthentication(application)
            UsersIdLocation.addRoute(this, service)
        }
    })

    lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    private lateinit var service: UsersService

    @MockK
    private lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET ${Path.UsersId}") {
            it("should return User") {
                val expected = User(id = 1)
                every { service.getUser(1) } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Get, "${Path.Users}/1")
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                        response shouldMatchAsJson expected
                    }
            }

            it("should return BadRequest") {
                ktorListener.handleJsonRequest(HttpMethod.Get, "${Path.Users}/string")
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return NotFound") {
                every { service.getUser(1) } returns null
                ktorListener.handleJsonRequest(HttpMethod.Get, "${Path.Users}/1")
                    .run {
                        response shouldHaveStatus HttpStatusCode.NotFound
                    }
            }
        }

        describe("PATCH ${Paths.usersIdPatch}") {
            it("should update User") {
                val expected = User(id = 1, screenName = "updated user")
                every { service.updateUser(1, "updated user") } returns expected
                ktorListener.handleJsonRequest(
                    HttpMethod.Patch,
                    Paths.usersIdPatch.assignPathParams("id" to expected.id)
                ) {
                    authorizationHelper.authorizeAsUser(this, expected)
                    setBody(buildJsonObject {
                        put("screenName", expected.screenName)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return BadRequest when request body is empty") {
                ktorListener.handleJsonRequest(HttpMethod.Patch, Paths.usersIdPatch.assignPathParams("id" to 1)) {
                    authorizationHelper.authorizeAsUser(this, User(id = 1))
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Forbidden when session user does not match to path user") {
                ktorListener.handleJsonRequest(HttpMethod.Patch, Paths.usersIdPatch.assignPathParams("id" to 1)) {
                    authorizationHelper.authorizeAsUser(this, User(id = 2))
                    setBody(buildJsonObject {
                        put("screenName", "name")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }

            it("should return NotFound when update user is not found") {
                every { service.updateUser(1, "name") } returns null
                ktorListener.handleJsonRequest(HttpMethod.Patch, Paths.usersIdPatch.assignPathParams("id" to 1)) {
                    authorizationHelper.authorizeAsUser(this, User(id = 1))
                    setBody(buildJsonObject {
                        put("screenName", "name")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE ${Paths.usersIdPatch}") {
            it("should delete User") {
                every { service.deleteUser(1) } just Runs
                ktorListener.handleJsonRequest(HttpMethod.Delete, Paths.usersIdPatch.assignPathParams("id" to 1)) {
                    authorizationHelper.authorizeAsUser(this, User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return Forbidden when session user does not match to path user") {
                every { service.deleteUser(1) } just Runs
                ktorListener.handleJsonRequest(HttpMethod.Delete, Paths.usersIdPatch.assignPathParams("id" to 1)) {
                    authorizationHelper.authorizeAsUser(this, User(id = 2))
                }.run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
        }
    }

}
