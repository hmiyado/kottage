package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.service.entries.EntriesService
import com.github.hmiyado.service.users.UsersService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.ktor.application.install
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.response.ApplicationResponse
import io.ktor.routing.routing
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@KtorExperimentalLocationsAPI
class RoutingTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@RoutingTest)
        with(application) {
            startKoin {
                modules(module {
                    single { entriesService }
                    single { usersService }
                })
            }
            AuthorizationHelper.installAuthentication(application)
            install(Locations)
            routing {
                routing()
            }
        }
    }, afterSpec = {
        stopKoin()
    })

    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var usersService: UsersService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("/") {
            it("should allow OPTIONS GET") {
                ktorListener
                    .handleRequest(HttpMethod.Options, "/")
                    .run {
                        response.shouldAllowMethods(HttpMethod.Options, HttpMethod.Get)
                    }
            }
        }

        describe("/entries") {
            it("should allow OPTIONS GET POST") {
                ktorListener
                    .handleRequest(HttpMethod.Options, "/entries")
                    .run {
                        response.shouldAllowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
                    }
            }
        }

        describe("/entries/{serialNumber}") {
            it("should allow OPTIONS GET PATCH DELETE") {
                ktorListener
                    .handleRequest(HttpMethod.Options, "/entries/1")
                    .run {
                        response.shouldAllowMethods(
                            HttpMethod.Options,
                            HttpMethod.Get,
                            HttpMethod.Patch,
                            HttpMethod.Delete
                        )
                    }
            }
        }

        describe("/users/{id}") {
            it("should allow OPTIONS GET") {
                ktorListener.handleRequest(HttpMethod.Options, "/users/1").run {
                    response.shouldAllowMethods(
                        HttpMethod.Options,
                        HttpMethod.Get
                    )
                }
            }
        }
    }

    private fun ApplicationResponse.shouldAllowMethods(vararg methods: HttpMethod) {
        val allowedMethods =
            headers["Allow"]?.split(",")?.map { it.trim() }?.map { HttpMethod.parse(it) } ?: emptyList()
        allowedMethods.shouldContainExactly(*methods)
    }
}
