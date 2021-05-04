package com.github.hmiyado.route

import com.github.hmiyado.helper.AuthorizationHelper
import com.github.hmiyado.helper.KtorApplicationTestListener
import com.github.hmiyado.service.articles.ArticlesService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.ktor.http.HttpMethod
import io.ktor.response.ApplicationResponse
import io.ktor.routing.routing
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class RoutingTest : DescribeSpec(), KoinTest {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@RoutingTest)
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
    }, afterSpec = {
        stopKoin()
    })

    @MockK
    lateinit var articlesService: ArticlesService

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

        describe("/articles") {
            it("should allow OPTIONS GET POST") {
                ktorListener
                    .handleRequest(HttpMethod.Options, "/articles")
                    .run {
                        response.shouldAllowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
                    }
            }
        }

        describe("/articles/{serialNumber}") {
            it("should allow OPTIONS GET DELETE") {
                ktorListener
                    .handleRequest(HttpMethod.Options, "/articles/1")
                    .run {
                        response.shouldAllowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Delete)
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
