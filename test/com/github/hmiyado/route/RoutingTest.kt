package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.service.articles.ArticlesService
import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
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
            it("should allow GET") {
                testApplicationEngine.application
                testApplicationEngine
                    .handleRequest(HttpMethod.Options, "/")
                    .run {
                        response.shouldHaveHeader("Allow", "OPTIONS, GET")
                    }
            }
        }
    }
}
