package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveHeader
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.OidcToken
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.models.AccountLink
import com.github.hmiyado.kottage.openapi.models.UserDetail
import com.github.hmiyado.kottage.openapi.models.Users
import com.github.hmiyado.kottage.repository.oauth.OauthGoogleRepository
import com.github.hmiyado.kottage.repository.oauth.OpenIdGoogleConfig
import com.github.hmiyado.kottage.route.users.UsersLocation.Companion.toResponseUser
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.test.KoinTest
import java.time.ZonedDateTime

class UsersLocationTest : DescribeSpec(), KoinTest {
    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var googleRepository: OauthGoogleRepository

    lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@UsersLocationTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
    }

    private val init: ApplicationTestBuilder.() -> Unit = {
        authorizationHelper.installSessionAuthentication(this)
        application {
            routing {
                UsersLocation(usersService, googleRepository).addRoute(this)
            }
        }
    }

    init {

        describe("GET ${Paths.usersGet}") {
            it("should return users") {
                testApplication {
                    init()
                    val expected = (1..10).map {
                        User(id = it.toLong(), screenName = "User${it}th")
                    }
                    every { usersService.getUsers() } returns expected
                    
                    val response = client.get(Paths.usersGet) {
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 99))
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson Users(items = expected.map { it.toResponseUser() })
                }
            }

            it("should not return users when unauthorized") {
                testApplication {
                    init()
                    
                    val response = client.get(Paths.usersGet)
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }

        describe("POST ${Paths.usersPost}") {
            it("should return user") {
                testApplication {
                    init()
                    val expected = User(id = 1, screenName = "expected")
                    every { usersService.createUser("expected", "password") } returns expected
                    
                    val response = client.post(Paths.usersPost) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("screenName", "expected")
                            put("password", "password")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, expected)
                    }
                    response shouldHaveStatus HttpStatusCode.Created
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
                testApplication {
                    init()
                    
                    val response = client.post(Paths.usersPost)
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Bad Request when screen name has already used") {
                testApplication {
                    init()
                    every {
                        usersService.createUser(
                            "expected",
                            "password",
                        )
                    } throws UsersService.DuplicateScreenNameException("expected")
                    
                    val response = client.post(Paths.usersPost) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("screenName", "expected")
                            put("password", "password")
                        }.toString())
                    }
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400()
                }
            }
        }

        describe("GET ${Paths.usersCurrentGet}") {
            it("should return current user") {
                testApplication {
                    init()
                    val expected = UserDetail(
                        id = 1,
                        screenName = "expected",
                        accountLinks = listOf(AccountLink(AccountLink.Service.Google, true)),
                    )
                    val user = User(expected.id, expected.screenName)
                    val config = OpenIdGoogleConfig("google", "", "", "")
                    every { usersService.getUser(expected.id) } returns user
                    every { usersService.getOidcToken(user) } returns listOf(OidcToken("google", "", "", ZonedDateTime.now(), ZonedDateTime.now()))
                    coEvery { googleRepository.getConfig() } returns config
                    
                    val response = client.get(Paths.usersCurrentGet) {
                        authorizeAsUserAndAdmin(authorizationHelper, user)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }
        }

        describe("POST ${Paths.signInPost}") {
            it("should return user") {
                testApplication {
                    init()
                    val expected = UserDetail(id = 1, screenName = "expected", accountLinks = listOf(AccountLink(AccountLink.Service.Google, true)))
                    val user = User(expected.id, expected.screenName)
                    every { usersService.authenticateUser("expected", "password") } returns user
                    coEvery { googleRepository.getConfig() } returns OpenIdGoogleConfig("google", "", "", "")
                    
                    val response = client.post(Paths.signInPost) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("screenName", "expected")
                            put("password", "password")
                        }.toString())
                    }
                    response shouldHaveStatus HttpStatusCode.OK
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
                testApplication {
                    init()
                    val expected = UserDetail(id = 1, screenName = "expected", accountLinks = listOf(AccountLink(AccountLink.Service.Google, true)))
                    val user = User(expected.id, expected.screenName)
                    every { usersService.authenticateUser("expected", "password") } returns user
                    every { usersService.getOidcToken(user) } returns listOf(OidcToken("google", "", "", ZonedDateTime.now(), ZonedDateTime.now()))
                    coEvery { googleRepository.getConfig() } returns OpenIdGoogleConfig("google", "", "", "")
                    
                    val response = client.post(Paths.signInPost) {
                        header("Content-Type", ContentType.Application.Json)
                        header("Cookie", "user_session=")
                        setBody(buildJsonObject {
                            put("screenName", "expected")
                            put("password", "password")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                    response.headers["Set-Cookie"] shouldNotContain "user_session=;"
                }
            }

            it("should return Bad Request when request body is illegal") {
                testApplication {
                    init()
                    
                    val response = client.post(Paths.signInPost)
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Conflict when request has already another user's session") {
                testApplication {
                    init()
                    val expected = User(id = 99)
                    every { usersService.authenticateUser("expected", "password") } returns expected
                    
                    val response = client.post(Paths.signInPost) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("screenName", "expected")
                            put("password", "password")
                        }.toString())
                        authorizeAsUserAndAdmin(authorizationHelper, User(id = 1))
                    }
                    response shouldHaveStatus HttpStatusCode.Conflict
                    response shouldMatchAsJson ErrorFactory.create409()
                }
            }

            it("should return Not Found when screen name and password has not matched") {
                testApplication {
                    init()
                    every {
                        usersService.authenticateUser(
                            "expected",
                            "password",
                        )
                    } returns null
                    
                    val response = client.post(Paths.signInPost) {
                        header("Content-Type", ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("screenName", "expected")
                            put("password", "password")
                        }.toString())
                    }
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("POST ${Paths.signOutPost}") {
            it("should clear user_session") {
                testApplication {
                    init()
                    val expected = User(id = 1, screenName = "expected")
                    coEvery { sessionStorage.invalidate(any()) } just Runs
                    
                    val response = client.post(Paths.signOutPost) {
                        authorizeAsUserAndAdmin(authorizationHelper, expected)
                    }
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
                testApplication {
                    init()
                    
                    val response = client.post(Paths.signOutPost)
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
