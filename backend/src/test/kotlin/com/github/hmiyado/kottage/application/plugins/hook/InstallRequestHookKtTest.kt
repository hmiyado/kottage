package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import com.github.hmiyado.kottage.application.plugins.clientsession.ClientSession
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class InstallRequestHookKtTest : DescribeSpec() {
    var httpClientRequestData: HttpRequestData? = null

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var randomGenerator: RandomGenerator

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@InstallRequestHookKtTest)
        httpClientRequestData = null
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(
                    module {
                        single {
                            val mockEngine =
                                MockEngine { request ->
                                    httpClientRequestData = request
                                    respondOk()
                                }
                            HttpClient(mockEngine)
                        }
                        single { sessionStorage }
                        single { randomGenerator }
                        single(named("HookConfigurations")) {
                            listOf(
                                HookConfiguration(
                                    name = "post",
                                    method = HttpMethod.Post,
                                    path = "/post",
                                    requestTo = "http://request.to/",
                                ),
                            )
                        }
                    },
                )
            }
        }
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        stopKoin()
    }

    private fun ApplicationTestBuilder.init() {
        application {
            routing {
                post("/post") { call.respond("ok") }
            }
            install(Sessions) {
                cookie<ClientSession>("client_session", storage = sessionStorage)
            }
            requestHook()
        }
    }

    init {
        describe("outgoing webhook") {
            it("should post to specified endpoint") {
                testApplication {
                    init()
                    client.post("/post")
                    httpClientRequestData?.method shouldBe HttpMethod.Post
                    httpClientRequestData?.url shouldBe Url("http://request.to/")
                }
            }
        }
    }
}
