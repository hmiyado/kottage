package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.users.UsersService
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest

class UsersIdLocationTest : DescribeSpec(), KoinTest {
    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    private lateinit var service: UsersService

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@UsersIdLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
    }

    private val init: ApplicationTestBuilder.() -> Unit = {
        authorizationHelper.installSessionAuthentication(this)
        application {
            routing {
                UsersIdLocation(service).addRoute(this)
            }
        }
    }

    init {

        describe("GET ${Paths.usersIdGet}") {
            it("should return User") {
                testApplication {
                    init()
                    val expected = User(id = 1)
                    every { service.getUser(1) } returns expected
                    
                    val response = client.get(Paths.usersIdGet.assignPathParams(1)) {
                        authorizeAsUserAndAdmin(authorizationHelper, User())
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return BadRequest") {
                testApplication {
                    init()
                    
                    val response = client.get(Paths.usersIdGet.assignPathParams("string")) {
                        authorizeAsUserAndAdmin(authorizationHelper, User())
                    }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("path parameter is not valid")
                }
            }

            it("should return Unauthorized") {
                testApplication {
                    init()
                    
                    val response = client.get(Paths.usersIdGet.assignPathParams(1))
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }

            it("should return NotFound") {
                testApplication {
                    init()
                    every { service.getUser(1) } returns null
                    
                    val response = client.get(Paths.usersIdGet.assignPathParams(1)) {
                        authorizeAsUserAndAdmin(authorizationHelper, User())
                    }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("PATCH ${Paths.usersIdPatch}") {
            it("should update User") {
                testApplication {
                    init()
                    val expected = User(id = 1, screenName = "updated_user")
                    every { service.updateUser(1, "updated_user") } returns expected
                    
                    val response = client.patch(Paths.usersIdPatch.assignPathParams("id" to expected.id)) {
                        setBody(buildJsonObject {
                            put("screenName", expected.screenName)
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, expected)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return BadRequest when request body is empty") {
                testApplication {
                    init()
                    
                    val response = client.patch(Paths.usersIdPatch.assignPathParams("id" to 1)) {
                        setBody("")
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                    }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Forbidden when session user does not match to path user") {
                testApplication {
                    init()
                    
                    val response = client.patch(Paths.usersIdPatch.assignPathParams("id" to 1)) {
                        setBody(buildJsonObject {
                            put("screenName", "name")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 2))
                    }
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }

            it("should return NotFound when update user is not found") {
                testApplication {
                    init()
                    every { service.updateUser(1, "name") } returns null
                    
                    val response = client.patch(Paths.usersIdPatch.assignPathParams("id" to 1)) {
                        setBody(buildJsonObject {
                            put("screenName", "name")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                    }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE ${Paths.usersIdDelete}") {
            it("should delete User") {
                testApplication {
                    init()
                    every { service.deleteUser(1) } just Runs
                    
                    val response = client.delete(Paths.usersIdDelete.assignPathParams("id" to 1)) {
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should return Forbidden when session user does not match to path user") {
                testApplication {
                    init()
                    every { service.deleteUser(1) } just Runs
                    
                    val response = client.delete(Paths.usersIdDelete.assignPathParams("id" to 1)) {
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 2))
                    }
                    response shouldHaveStatus HttpStatusCode.Forbidden
                }
            }
        }
    }
}
