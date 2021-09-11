package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.RoutingTestHelper
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Health
import com.github.hmiyado.kottage.service.health.HealthService
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.nio.charset.Charset

class HealthLocationTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@HealthLocationTest)
        RoutingTestHelper.setupRouting(application) {
            HealthLocation.addRoute(this, healthService)
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
                ktorListener.handleJsonRequest(HttpMethod.Get, "/health").run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.shouldHaveContentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
                    response shouldMatchAsJson expected
                }
            }
        }
    }

}
