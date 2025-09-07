package com.github.hmiyado.kottage.application.plugins.hook

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.TestApplication
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class RequestHookTest : DescribeSpec() {
    private fun ApplicationTestBuilder.init() {
        with(application) {
            routing {
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
    }

    @MockK
    lateinit var hook1: () -> Unit

    @MockK
    lateinit var hook2: () -> Unit

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@RequestHookTest)
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        clearAllMocks()
    }

    init {
        describe("RequestHook") {
            it("should run hook") {
                testApplication {
                    init()
                    every { hook1() } just Runs
                    client.get("/") { }
                    verify { hook1() }
                }
            }
            it("should run multiple hook") {
                testApplication {
                    init()
                    every { hook1() } just Runs
                    every { hook2() } just Runs
                    client.get("/")
                    client.post("/test")
                    verify { hook1() }
                    verify { hook2() }
                }
            }
            it("should run successfully when hook throws") {
                shouldNotThrow<Exception> {
                    testApplication {
                        init()
                        client.get("/exception")
                    }
                }
            }
        }
    }
}
