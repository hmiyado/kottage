package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.routing.routing
import io.ktor.serialization.json
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
        with(application) {
            install(ContentNegotiation) {
                json()
            }
            AuthorizationHelper.installSessionAuthentication(this, usersService, sessionStorage)
            routing {
                UsersLocation.addRoute(this, usersService)
            }
        }

    })

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET /users") {
            it("should return users") {
                val expected = (1..10).map {
                    User(id = it.toLong(), screenName = "${it}thUser")
                }
                every { usersService.getUsers() } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Get, "/users").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson expected
                }
            }
        }

        describe("POST /users") {
            it("should return user") {
                val expected = User(id = 1, screenName = "expected")
                every { usersService.createUser("expected", "password") } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Post, "/users") {
                    AuthorizationHelper.authorizeAsUser(this, usersService, sessionStorage, expected)
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.shouldHaveHeader("Location", "http://localhost/users/1")
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
                ktorListener.handleJsonRequest(HttpMethod.Post, "/users").run {
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
                ktorListener.handleJsonRequest(HttpMethod.Post, "/users") {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }
        }

        describe("POST /signIn") {
            it("should return user") {
                val expected = User(id = 1, screenName = "expected")
                every { usersService.authenticateUser("expected", "password") } returns expected
                ktorListener.handleJsonRequest(HttpMethod.Post, "/signIn") {
                    AuthorizationHelper.authorizeAsUser(this, usersService, sessionStorage, expected)
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
                    setCookie shouldContain ("Path" to "/")
                }
            }

            it("should return Bad Request when request body is illegal") {
                ktorListener.handleJsonRequest(HttpMethod.Post, "/signIn").run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return Not Found when screen name and password has not matched") {
                every {
                    usersService.authenticateUser(
                        "expected",
                        "password"
                    )
                } returns null
                ktorListener.handleJsonRequest(HttpMethod.Post, "/signIn") {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("POST /signOut") {
            it("should clear user_session") {
                val expected = User(id = 1, screenName = "expected")
                coEvery { sessionStorage.invalidate(any()) } just Runs
                ktorListener.handleJsonRequest(HttpMethod.Post, "/signOut") {
                    AuthorizationHelper.authorizeAsUser(this, usersService, sessionStorage, expected)
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
                ktorListener.handleJsonRequest(HttpMethod.Post, "/signOut").run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
