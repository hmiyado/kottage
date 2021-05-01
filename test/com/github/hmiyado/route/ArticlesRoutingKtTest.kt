package com.github.hmiyado.route

import com.github.hmiyado.repository.repositoryModule
import com.github.hmiyado.service.serviceModule
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.koin.KoinListener
import io.kotest.matchers.string.shouldContain
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.test.KoinTest

class ArticlesRoutingKtTest : DescribeSpec(), KoinTest {
    private lateinit var testApplicationEngine: TestApplicationEngine

    override fun listeners(): List<TestListener> = listOf(KoinListener(listOf(repositoryModule, serviceModule)))

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        testApplicationEngine = TestApplicationEngine()
        testApplicationEngine.start()
        with(testApplicationEngine) {
            application.routing {
                articles()
            }
        }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        testApplicationEngine.stop(0L, 0L)
    }

    init {
        describe("route /articles") {
            it("should return articles") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Get, "/articles")) {
                        response shouldHaveStatus HttpStatusCode.OK
                    }
                }
            }
        }

        describe("route POST /articles") {
            it("should return new article") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Post, "/articles") {
                        val map = mapOf(
                            "title" to "title1",
                            "body" to "body1"
                        )
                        val body = Json.encodeToString(map)
                        setBody(body)
                    }) {
                        response shouldHaveStatus HttpStatusCode.OK
                        response.content?.let {
                            it shouldContainJsonKey "title"
                            it shouldContain "title1"
                            it shouldContainJsonKey "body"
                            it shouldContain "body1"
                            it shouldContainJsonKey "dateTime"
                        }
                    }
                }
            }

            it("should return Bad Request") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Post, "/articles") {
                        setBody("")
                    }) {
                        response shouldHaveStatus HttpStatusCode.BadRequest
                    }
                }
            }
        }
    }
}
