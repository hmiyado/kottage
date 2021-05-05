package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.service.entries.EntriesService
import com.github.hmiyado.service.users.UsersService
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
        describe("routing") {
            val parameters = listOf(
                RoutingParameters("/", listOf(HttpMethod.Options, HttpMethod.Get)),
                RoutingParameters("/entries", listOf(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)),
                RoutingParameters(
                    "/entries/1",
                    listOf(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
                ),
                RoutingParameters("/users", listOf(HttpMethod.Options, HttpMethod.Get)),
                RoutingParameters("/users/1", listOf(HttpMethod.Options, HttpMethod.Get))
            )
            forAll<RoutingParameters>(
                *(parameters.map { it.description to it }.toTypedArray())
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

    data class RoutingParameters(
        val path: String,
        val allowMethods: List<HttpMethod>,
    ) {
        val description = "$path should allow ${allowMethods.joinToString(",") { it.value }}"
    }
}
