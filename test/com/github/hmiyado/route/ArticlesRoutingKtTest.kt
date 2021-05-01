package com.github.hmiyado.route

import com.github.hmiyado.repository.repositoryModule
import com.github.hmiyado.service.serviceModule
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainAll
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.core.context.startKoin

class ArticlesRoutingKtTest : DescribeSpec() {
    private lateinit var testApplicationEngine: TestApplicationEngine

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        startKoin {
            modules(repositoryModule, serviceModule)
        }
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
                        val jsonBody =
                            response.content?.let { Json.parseToJsonElement(it).jsonObject }?.toMap() ?: emptyMap()
                        jsonBody shouldNotContainAll mapOf(
                            "title" to "title1",
                            "body" to "body1"
                        )
                        jsonBody shouldContainKey "dateTime"
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
