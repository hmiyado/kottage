package com.github.hmiyado.route.users

import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.helper.shouldMatchAsJson
import com.github.hmiyado.model.User
import com.github.hmiyado.service.users.UsersService
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.serialization.json
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

@KtorExperimentalLocationsAPI
class UsersIdLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@UsersIdLocationTest)

        with(application) {
            install(Locations)
            install(ContentNegotiation) {
                json()
            }
            install(Routing) {
                UsersIdLocation.addRoute(this, service)
            }
        }
    })

    @MockK
    private lateinit var service: UsersService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET /users/{id}") {
            it("should return User") {
                val expected = User(id = 1)
                every { service.getUser(1) } returns expected
                ktorListener.handleRequest(HttpMethod.Get, "/users/1")
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                        response shouldMatchAsJson expected
                    }
            }

            it("should return BadRequest") {
                ktorListener.handleRequest(HttpMethod.Get, "/users/string")
                    .run {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
            }

            it("should return NotFound") {
                every { service.getUser(1) } returns null
                ktorListener.handleRequest(HttpMethod.Get, "/users/1")
                    .run {
                        response shouldHaveStatus HttpStatusCode.NotFound
                    }
            }
        }
    }

}
