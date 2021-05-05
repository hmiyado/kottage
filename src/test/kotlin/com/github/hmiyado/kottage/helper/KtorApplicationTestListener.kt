package com.github.hmiyado.kottage.helper

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.handleRequest

class KtorApplicationTestListener(
    private val beforeSpec: TestApplicationEngine.() -> Unit = {},
    private val afterSpec: TestApplicationEngine.() -> Unit = {}
) : TestListener {
    private lateinit var testApplicationEngine: TestApplicationEngine

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        testApplicationEngine = TestApplicationEngine()
        testApplicationEngine.start()
        beforeSpec(testApplicationEngine)
    }

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        afterSpec(testApplicationEngine)
        testApplicationEngine.stop(0L, 0L)
    }

    fun handleRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {}
    ): TestApplicationCall {
        return testApplicationEngine.handleRequest(method, uri, setup)
    }
}
