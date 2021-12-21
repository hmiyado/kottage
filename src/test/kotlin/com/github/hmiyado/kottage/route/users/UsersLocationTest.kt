package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.models.Users
import com.github.hmiyado.kottage.route.users.UsersLocation.Companion.toResponseUser
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.testing.setBody
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import java.nio.charset.Charset
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UsersLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@UsersLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)

        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        }) {
            UsersLocation(usersService).addRoute(this)
        }
    })

    lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var adminsService: AdminsService

    @MockK
    lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET ${Paths.usersGet}") {
            it("should return users") {
                val expected = (1..10).map {
                    User(id = it.toLong(), screenName = "${it}thUser")
                }
                every { usersService.getUsers() } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Get, Paths.usersGet) {
                    authorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        User(id = 99)
                    )
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson Users(items = expected.map { it.toResponseUser() })
                }
            }

            it("should not return users when unauthorized") {
                ktorListener.handleJsonRequest(HttpMethod.Get, Paths.usersGet) {
                    // unauthorized
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }

        describe("POST ${Paths.usersPost}") {
            it("should return user") {
                val expected = User(id = 1, screenName = "expected")
                every { usersService.createUser("expected", "password") } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.usersPost) {
                    authorizationHelper.authorizeAsUser(this, expected)
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.shouldHaveHeader("Location", "http://localhost${Paths.usersGet}/1")
                    response shouldMatchAsJson expected
                    val setCookie = response.headers["Set-Cookie"]
                        ?.split(";")
                        ?.map { it.trim() }
                        ?.associate {
                            if (it.contains("=")) {
                                val (key, value) = it.split("=")
                                key to value
                            } else {
                                it to ""
                            }
                        } ?: emptyMap()
                    setCookie shouldContainKey "user_session"
                    setCookie shouldContain ("Path" to "/")
                }
            }

            it("should return Bad Request when request body is illegal") {
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.usersPost).run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Bad Request when screen name has already used") {
                every {
                    usersService.createUser(
                        "expected",
                        "password"
                    )
                } throws UsersService.DuplicateScreenNameException("expected")
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.usersPost) {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }
        }

        describe("GET ${Paths.usersCurrentGet}") {
            it("should return current user") {
                val expected = User(id = 1, screenName = "expected")
                every { usersService.getUser(expected.id) } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Get, Paths.usersCurrentGet) {
                    authorizationHelper.authorizeAsUser(this, expected)
                    setBody(buildJsonObject {}.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson expected
                }
            }
        }

        describe("POST ${Paths.signInPost}") {
            it("should return user") {
                val expected = User(id = 1, screenName = "expected")
                every { usersService.authenticateUser("expected", "password") } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signInPost) {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson expected
                    val setCookie = response.headers["Set-Cookie"]
                        ?.split(";")
                        ?.map { it.trim() }
                        ?.associate {
                            if (it.contains("=")) {
                                val (key, value) = it.split("=")
                                key to value
                            } else {
                                it to ""
                            }
                        } ?: emptyMap()
                    setCookie shouldContainKey "user_session"
                    setCookie["user_session"] shouldMatch "[0-9a-z]+"
                    setCookie shouldContain ("Path" to "/")
                }
            }

            it("should return valid user session if request user_session is empty (Cookie: user_session=)") {
                val expected = User(id = 1)
                every { usersService.authenticateUser("expected", "password") } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signInPost) {
                    authorizationHelper.authorizeAsUser(this, User(id = 1))
                    addHeader("Cookie", "user_session=")
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.headers["Set-Cookie"] shouldNotContain "user_session=;"
                }
            }

            it("should return Bad Request when request body is illegal") {
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signInPost).run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Conflict when request has already another user's session") {
                val expected = User(id = 99)
                every { usersService.authenticateUser("expected", "password") } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signInPost) {
                    authorizationHelper.authorizeAsUser(this, User(id = 1))
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Conflict
                }
            }

            it("should return Not Found when screen name and password has not matched") {
                every {
                    usersService.authenticateUser(
                        "expected",
                        "password"
                    )
                } returns null
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signInPost) {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("POST ${Paths.signOutPost}") {
            it("should clear user_session") {
                val expected = User(id = 1, screenName = "expected")
                coEvery { sessionStorage.invalidate(any()) } just Runs
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signOutPost) {
                    authorizationHelper.authorizeAsUser(this, expected)
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    val setCookie = response.headers["Set-Cookie"]
                        ?.split(";")
                        ?.map { it.trim() }
                        ?.associate {
                            if (it.contains("=")) {
                                val (key, value) = it.split("=")
                                key to value
                            } else {
                                it to ""
                            }
                        } ?: emptyMap()
                    setCookie shouldContain ("user_session" to "")
                }
            }

            it("should return OK when no user_session") {
                ktorListener.handleJsonRequest(HttpMethod.Post, Paths.signOutPost).run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
