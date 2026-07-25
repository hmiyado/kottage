package com.github.hmiyado.kottage.application.plugins.hook

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

class RequestHookTest : DescribeSpec() {
    private companion object {
        const val SLOW_HOOK_DELAY = 300L
    }

    private fun ApplicationTestBuilder.init() {
        with(application) {
            routing {
                // ステータスを明示する経路と、本文だけを返してエンジンに200を補わせる
                // 経路の両方でフックが動くことを確かめる（後者は送信パイプラインの
                // どのフェーズでも status() が null のままになる）。
                get("/") { call.respond(HttpStatusCode.OK) }
                post("/test") { call.respond("ok") }
                post("/forbidden") { call.respond(HttpStatusCode.Forbidden) }
                post("/slow-hook") { call.respond(HttpStatusCode.Created) }
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
                hook(HttpMethod.Post, "/forbidden") {
                    hookOnFailure()
                }
                hook(HttpMethod.Post, "/slow-hook") {
                    delay(SLOW_HOOK_DELAY)
                    hookSlow()
                }
            }
        }
    }

    @MockK
    lateinit var hook1: () -> Unit

    @MockK
    lateinit var hook2: () -> Unit

    @MockK
    lateinit var hookOnFailure: () -> Unit

    @MockK
    lateinit var hookSlow: () -> Unit

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@RequestHookTest)
    }

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
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
            it("should not run hook when the response is not successful") {
                testApplication {
                    init()
                    client.post("/forbidden")
                    // 以前はステータスを見ずに発火していたため、CSRFで弾かれた403のような
                    // 「実際には何も作られていない」リクエストでもフックが走っていた。
                    verify(exactly = 0) { hookOnFailure() }
                }
            }
            it("should finish the hook before sending the response") {
                testApplication {
                    init()
                    every { hookSlow() } just Runs
                    val start = TimeSource.Monotonic.markNow()
                    client.post("/slow-hook")
                    val elapsed = start.elapsedNow().inWholeMilliseconds
                    // レスポンスを受け取った時点でフックが完了していることの確認。
                    // Lambdaはレスポンス送出でinvocationが終了し実行環境がfreezeされるため、
                    // レスポンス後に走るフックは送信されないまま凍結する。
                    verify { hookSlow() }
                    elapsed shouldBeGreaterThanOrEqual SLOW_HOOK_DELAY
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
