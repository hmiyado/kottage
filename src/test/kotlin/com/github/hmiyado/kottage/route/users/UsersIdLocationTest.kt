package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.serialization.json
import io.ktor.server.testing.setBody
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

        describe("PATCH /users/{id}") {
            it("should update User") {
                val expected = User(id = 1, screenName = "updated user")
                every { service.updateUser(1, "updated user") } returns expected
                ktorListener.handleRequest(HttpMethod.Patch, "/users/${expected.id}") {
                    addHeader("Content-Type", ContentType.Application.Json.toString())
                    setBody(buildJsonObject {
                        put("screenName", expected.screenName)
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }

            it("should return BadRequest") {
                ktorListener.handleRequest(HttpMethod.Patch, "/users/1") {
                    setBody("")
                }.run {
                    response shouldHaveStatus HttpStatusCode.BadRequest
                }
            }

            it("should return NotFound") {
                every { service.updateUser(1, "name") } returns null
                ktorListener.handleRequest(HttpMethod.Patch, "/users/1") {
                    addHeader("Content-Type", ContentType.Application.Json.toString())
                    setBody(buildJsonObject {
                        put("screenName", "name")
                    }.toString())
                }.run {
                    response shouldHaveStatus HttpStatusCode.NotFound
                }
            }
        }

        describe("DELETE /users/{id}") {
            it("should delete User") {
                every { service.deleteUser(1) } just Runs
                ktorListener.handleRequest(HttpMethod.Delete, "/users/1")
                    .run {
                        response shouldHaveStatus HttpStatusCode.OK
                    }
            }
        }
    }

}
