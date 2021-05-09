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
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.testing.setBody
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.nio.charset.Charset
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UsersLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@UsersLocationTest)
        with(application) {
            install(ContentNegotiation) {
                // this must be first because this becomes default ContentType
                json(contentType = ContentType.Application.Json)
                json(contentType = ContentType.Any)
                json(contentType = ContentType.Text.Any)
                json(contentType = ContentType.Text.Plain)
            }
            AuthorizationHelper.installAuthentication(this)
            routing {
                UsersLocation.addRoute(this, usersService)
            }
        }

    })

    @MockK
    lateinit var usersService: UsersService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET /users") {
            it("should return users") {
                val expected = (1..10).map {
                    User(id = it.toLong(), screenName = "${it}thUser")
                }
                every { usersService.getUsers() } returns expected
                ktorListener.handleRequest(HttpMethod.Get, "/users").run {
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
                ktorListener.handleRequest(HttpMethod.Post, "/users") {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.Created
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response.shouldHaveHeader("Location", "http://localhost/users/1")
                    response shouldMatchAsJson expected
                }
            }

            it("should return Bad Request when request body is illegal") {
                ktorListener.handleRequest(HttpMethod.Post, "/users").run {
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
                ktorListener.handleRequest(HttpMethod.Post, "/users") {
                    setBody(buildJsonObject {
                        put("screenName", "expected")
                        put("password", "password")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }
        }
    }
}
