package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.mockk.MockKAnnotations
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class InstallRequestHookKtTest : DescribeSpec() {
    val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@InstallRequestHookKtTest)
        startKoin {
            modules(
                module {
                    single {
                        val mockEngine = MockEngine { request ->
                            httpClientRequestData = request
                            respondOk()
                        }
                        HttpClient(mockEngine)
                    }
                    single(named("HookConfigurations")) {
                        listOf(
                            HookConfiguration(
                                name = "post",
                                method = HttpMethod.Post,
                                path = "/post",
                                requestTo = "http://request.to/",
                            )
                        )
                    }
                },
            )
        }
        with(application) {
            install(Routing) {
                post("/post") { call.respond("ok") }
            }
            requestHook()
        }
    }, afterSpec = {
        stopKoin()
    })

    var httpClientRequestData: HttpRequestData? = null

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        httpClientRequestData = null
    }

    init {
        describe("installRequestHook") {
            it("should post to specified endpoint") {
                ktorListener.handleRequest(HttpMethod.Post, "/post")
                httpClientRequestData?.method shouldBe HttpMethod.Post
                httpClientRequestData?.url shouldBe Url("http://request.to/")
            }
        }
    }
}
