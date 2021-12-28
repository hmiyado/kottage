package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import com.github.hmiyado.kottage.application.plugins.csrf.ClientSession
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
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
                    single { sessionStorage }
                    single { randomGenerator }
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
            install(Sessions) {
                cookie<ClientSession>("client_session", storage = sessionStorage)
            }
            requestHook()
        }
    }, afterSpec = {
        stopKoin()
    })

    var httpClientRequestData: HttpRequestData? = null

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var randomGenerator: RandomGenerator

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        httpClientRequestData = null
    }

    init {
        describe("outgoing webhook") {
            it("should post to specified endpoint") {
                ktorListener.handleRequest(HttpMethod.Post, "/post")
                httpClientRequestData?.method shouldBe HttpMethod.Post
                httpClientRequestData?.url shouldBe Url("http://request.to/")
            }
        }

        describe("insert client session") {
            it("should set client session if it is absent") {
                val expected = "session"
                coEvery { sessionStorage.read<ClientSession>(any(), any()) } throws NoSuchElementException()
                coEvery { sessionStorage.write(any(), any()) } just Runs
                every { randomGenerator.generateString() } returns expected
                ktorListener.handleRequest(HttpMethod.Post, "/post").run {
                    response.headers["Set-Cookie"] shouldContain Regex("client_session=[0-9a-z]+")
                }
            }
            it("should not set client session if it is not absent") {
                val session = "session"
                coEvery { sessionStorage.read<ClientSession>(session, any()) } returns ClientSession(session)
                ktorListener.handleRequest(HttpMethod.Post, "/post") {
                    addHeader("Cookie", "client_session=$session")
                }.run {
                    response.headers["Set-Cookie"] shouldContain Regex("client_session=[0-9a-z]+")
                }
            }
        }
    }
}
