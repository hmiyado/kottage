package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.core.datatest.forAll
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
        val testCases = listOf(
            RoutingTestCase.from("/", HttpMethod.Options, HttpMethod.Get),
            RoutingTestCase.from("/entries", HttpMethod.Options, HttpMethod.Get, HttpMethod.Post),
            RoutingTestCase.from("/entries/1", HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete),
            RoutingTestCase.from("/users", HttpMethod.Options, HttpMethod.Get, HttpMethod.Post),
            RoutingTestCase.from("/users/1", HttpMethod.Options, HttpMethod.Get)
        )
        describe("routing") {
            forAll<RoutingTestCase>(
                *(testCases.map { it.description to it }.toTypedArray())
            ) { (path, methods) ->
                ktorListener
                    .handleRequest(HttpMethod.Options, path)
                    .run {
                        response.shouldAllowMethods(*methods.toTypedArray())
                    }
            }
        }
    }

    private fun ApplicationResponse.shouldAllowMethods(vararg methods: HttpMethod) {
        val allowedMethods =
            headers["Allow"]?.split(",")?.map { it.trim() }?.map { HttpMethod.parse(it) } ?: emptyList()
        allowedMethods.shouldContainExactly(*methods)
    }

    data class RoutingTestCase(
        val path: String,
        val allowMethods: List<HttpMethod>,
    ) {
        val description = "$path should allow ${allowMethods.joinToString(",") { it.value }}"

        companion object {
            fun from(path: String, vararg methods: HttpMethod): RoutingTestCase = RoutingTestCase(
                path,
                methods.toList()
            )
        }
    }
}
