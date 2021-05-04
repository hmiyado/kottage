package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.service.articles.ArticlesService
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.ktor.http.HttpMethod
import io.ktor.response.ApplicationResponse
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class RoutingTest : DescribeSpec(), KoinTest {
    @MockK
    lateinit var articlesService: ArticlesService
    private lateinit var testApplicationEngine: TestApplicationEngine

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this)

        testApplicationEngine = TestApplicationEngine()
        testApplicationEngine.start()
        with(testApplicationEngine) {
            with(application) {
                startKoin {
                    modules(module {
                        single { articlesService }
                    })
                }
                AuthorizationHelper.installAuthentication(application)
                routing {
                    routing()
                }
            }
        }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        testApplicationEngine.run {
            stopKoin()
        }
        testApplicationEngine.stop(0L, 0L)
    }

    init {
        describe("/") {
            it("should allow OPTIONS GET") {
                testApplicationEngine
                    .handleRequest(HttpMethod.Options, "/")
                    .run {
                        response.shouldAllowMethods(HttpMethod.Options, HttpMethod.Get)
                    }
            }
        }

        describe("/articles") {
            it("should allow OPTIONS GET POST") {
                testApplicationEngine
                    .handleRequest(HttpMethod.Options, "/articles")
                    .run {
                        response.shouldAllowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
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
