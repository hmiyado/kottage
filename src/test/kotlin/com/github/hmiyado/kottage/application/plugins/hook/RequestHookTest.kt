package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class RequestHookTest : DescribeSpec() {
    val ktorListener = KtorApplicationTestListener(
        beforeSpec = {
            MockKAnnotations.init(this@RequestHookTest)
            with(application) {
                install(Routing) {
                    get("/") {}
                    post("/test") {}
                }
                install(RequestHook) {
                    hook(HttpMethod.Get, "/") {
                        hook1()
                    }
                    hook(HttpMethod.Post, "/test") {
                        hook2()
                    }
                    hook(HttpMethod.Get, "/exception") {
                        throw Exception()
                    }
                }
            }
        },
    )

    @MockK
    lateinit var hook1: () -> Unit

    @MockK
    lateinit var hook2: () -> Unit

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        clearAllMocks()
    }

    init {
        describe("RequestHook") {
            it("should run hook") {
                every { hook1() } just Runs
                ktorListener.handleRequest(HttpMethod.Get, "/") {}
                verify { hook1() }
            }
            it("should run multiple hook") {
                every { hook1() } just Runs
                every { hook2() } just Runs
                ktorListener.handleRequest(HttpMethod.Get, "/") {}
                ktorListener.handleRequest(HttpMethod.Post, "/test") {}
                verify { hook1() }
                verify { hook2() }
            }
            it("should run successfully when hook throws") {
                shouldNotThrow<Exception> {
                    ktorListener.handleRequest(HttpMethod.Get, "/exception") {}
                }
            }
        }
    }
}
