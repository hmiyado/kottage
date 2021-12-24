package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.health.HealthService
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.datatest.forAll
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.ktor.http.HttpMethod
import io.ktor.response.ApplicationResponse
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class RoutingTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@RoutingTest)
        startKoin {
            modules(
                module {
                    single { entriesService }
                    single { usersService }
                    single { adminsService }
                    single { healthService }
                    single { entriesCommentsService }
                },
                routeModule
            )
        }
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)
        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        }) {
            application.routing()
        }
    }, afterSpec = {
        stopKoin()
    })

    lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    lateinit var entriesService: EntriesService

    @MockK
    lateinit var entriesCommentsService: EntriesCommentsService

    @MockK
    lateinit var usersService: UsersService

    @MockK
    lateinit var adminsService: AdminsService

    @MockK
    lateinit var healthService: HealthService

    @MockK
    lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        val testCases = listOf(
            RoutingTestCase.from(RootLocation.path, HttpMethod.Options, HttpMethod.Get),
            RoutingTestCase.from(Paths.entriesGet, HttpMethod.Options, HttpMethod.Get, HttpMethod.Post),
            RoutingTestCase.from(
                Paths.entriesSerialNumberGet.assignPathParams(1),
                HttpMethod.Options,
                HttpMethod.Get,
                HttpMethod.Patch,
                HttpMethod.Delete
            ),
            RoutingTestCase.from(
                Paths.entriesSerialNumberCommentsGet.assignPathParams(1),
                HttpMethod.Options,
                HttpMethod.Get,
                HttpMethod.Post
            ),
            RoutingTestCase.from(
                Paths.entriesSerialNumberCommentsCommentIdDelete.assignPathParams(1, 1),
                HttpMethod.Options,
                HttpMethod.Delete,
            ),
            RoutingTestCase.from(Paths.usersGet, HttpMethod.Options, HttpMethod.Get, HttpMethod.Post),
            RoutingTestCase.from(
                Paths.usersIdPatch.assignPathParams(1),
                HttpMethod.Options,
                HttpMethod.Get,
                HttpMethod.Patch,
                HttpMethod.Delete
            ),
            RoutingTestCase.from(Paths.healthGet, HttpMethod.Options, HttpMethod.Get),
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
