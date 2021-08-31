package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Health
import com.github.hmiyado.kottage.service.health.HealthService
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.nio.charset.Charset

class HealthLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@HealthLocationTest)
        with(application) {
            install(ContentNegotiation) {
                // this must be first because this becomes default ContentType
                json(contentType = ContentType.Application.Json)
                json(contentType = ContentType.Any)
                json(contentType = ContentType.Text.Any)
                json(contentType = ContentType.Text.Plain)
            }
            routing {
                HealthLocation.addRoute(this, healthService)
            }
        }

    })

    @MockK
    lateinit var healthService: HealthService

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("GET /health") {
            it("should return OK") {
                val expected = Health()
                every { healthService.getHealth() } returns expected
                ktorListener.handleRequest(HttpMethod.Get, "/health").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson expected
                }
            }
        }
    }

}
