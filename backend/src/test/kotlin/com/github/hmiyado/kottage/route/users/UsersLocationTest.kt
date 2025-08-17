package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.post
import com.github.hmiyado.kottage.helper.routing
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
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.serialization.json.put
import java.time.ZonedDateTime

class UsersLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    override fun listeners(): List<TestListener> = listOf(listener)

    @MockK
    lateinit var googleRepository: OauthGoogleRepository

    init {
        MockKAnnotations.init(this)
        routing {
            UsersLocation(usersService, googleRepository).addRoute(this)
        }

        describe("GET ${Paths.usersGet}") {
            it("should return users") {
                val expected = (1..10).map {
                    User(id = it.toLong(), screenName = "User${it}th")
                }
                every { usersService.getUsers() } returns expected
                get(Paths.usersGet) {
                    authorizeAsAdmin(User(id = 99))
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson Users(items = expected.map { it.toResponseUser() })
                }
            }

            it("should not return users when unauthorized") {
                get(Paths.usersGet).run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }

        describe("POST ${Paths.usersPost}") {
            it("should return user") {
                val expected = User(id = 1, screenName = "expected")
                every { usersService.createUser("expected", "password") } returns expected
                post(
                    Paths.usersPost,
                    {
                        put("screenName", "expected")
                        put("password", "password")
                    },
                ) {
                    authorizeAsUser(expected)
                }.run {
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
                post(Paths.usersPost).run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Bad Request when screen name has already used") {
                every {
                    usersService.createUser(
                        "expected",
                        "password",
                    )
                } throws UsersService.DuplicateScreenNameException("expected")
                post(
                    Paths.usersPost,
                    {
                        put("screenName", "expected")
                        put("password", "password")
                    },
                ).run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400()
                }
            }
        }

        describe("GET ${Paths.usersCurrentGet}") {
            it("should return current user") {
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
                get(Paths.usersCurrentGet) {
                    authorizeAsUser(user)
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }
        }

        describe("POST ${Paths.signInPost}") {
            it("should return user") {
                val expected = UserDetail(id = 1, screenName = "expected", accountLinks = listOf(AccountLink(AccountLink.Service.Google, true)))
                val user = User(expected.id, expected.screenName)
                every { usersService.authenticateUser("expected", "password") } returns user
                coEvery { googleRepository.getConfig() } returns OpenIdGoogleConfig("google", "", "", "")
                post(
                    Paths.signInPost,
                    {
                        put("screenName", "expected")
                        put("password", "password")
                    },
                ).run {
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
                val expected = UserDetail(id = 1, screenName = "expected", accountLinks = listOf(AccountLink(AccountLink.Service.Google, true)))
                val user = User(expected.id, expected.screenName)
                every { usersService.authenticateUser("expected", "password") } returns user
                every { usersService.getOidcToken(user) } returns listOf(OidcToken("google", "", "", ZonedDateTime.now(), ZonedDateTime.now()))
                coEvery { googleRepository.getConfig() } returns OpenIdGoogleConfig("google", "", "", "")
                post(
                    Paths.signInPost,
                    {
                        put("screenName", "expected")
                        put("password", "password")
                    },
                ) {
                    authorizeAsUser(User(id = 1))
                    addHeader("Cookie", "user_session=")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.headers["Set-Cookie"] shouldNotContain "user_session=;"
                }
            }

            it("should return Bad Request when request body is illegal") {
                post(Paths.signInPost).run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                    response shouldMatchAsJson ErrorFactory.create400("request body is not valid")
                }
            }

            it("should return Conflict when request has already another user's session") {
                val expected = User(id = 99)
                every { usersService.authenticateUser("expected", "password") } returns expected
                post(
                    Paths.signInPost,
                    {
                        put("screenName", "expected")
                        put("password", "password")
                    },
                ) {
                    authorizeAsUser(User(id = 1))
                }.run {
                    response shouldHaveStatus HttpStatusCode.Conflict
                    response shouldMatchAsJson ErrorFactory.create409()
                }
            }

            it("should return Not Found when screen name and password has not matched") {
                every {
                    usersService.authenticateUser(
                        "expected",
                        "password",
                    )
                } returns null
                post(
                    Paths.signInPost,
                    {
                        put("screenName", "expected")
                        put("password", "password")
                    },
                ).run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("POST ${Paths.signOutPost}") {
            it("should clear user_session") {
                val expected = User(id = 1, screenName = "expected")
                coEvery { sessionStorage.invalidate(any()) } just Runs
                post(Paths.signOutPost) {
                    authorizeAsUser(expected)
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
                post(Paths.signOutPost).run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
