package com.github.hmiyado.kottage.helper

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.handleRequest

class KtorApplicationTestListener(
    beforeSpec: TestApplicationEngine.() -> Unit = {},
    private val afterSpec: TestApplicationEngine.() -> Unit = {},
    private val beforeTest: TestApplicationEngine.() -> Unit = {},
    private val afterTest: TestApplicationEngine.() -> Unit = {},
) : TestListener {
    private lateinit var testApplicationEngine: TestApplicationEngine
    val beforeSpecListeners: MutableList<TestApplicationEngine.() -> Unit> = mutableListOf(beforeSpec)

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        testApplicationEngine = TestApplicationEngine()
        testApplicationEngine.start()
        for (listener in beforeSpecListeners) {
            listener(testApplicationEngine)
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        afterSpec(testApplicationEngine)
        testApplicationEngine.stop(0L, 0L)
    }

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        beforeTest(testApplicationEngine)
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        afterTest(testApplicationEngine)
    }

    fun handleRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {}
    ): TestApplicationCall {
        return testApplicationEngine.handleRequest(method, uri, setup)
    }

    fun handleJsonRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {}
    ): TestApplicationCall {
        return handleRequest(method, uri) {
            addHeader("Content-Type", ContentType.Application.Json.toString())
            setup()
        }
    }

}
