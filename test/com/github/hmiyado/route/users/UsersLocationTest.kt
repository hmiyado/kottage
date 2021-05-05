package com.github.hmiyado.route.users

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.helper.shouldMatchAsJson
import com.github.hmiyado.model.User
import com.github.hmiyado.service.users.UsersService
import io.kotest.assertions.ktor.shouldHaveContentType
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
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.nio.charset.Charset

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
    }
}
