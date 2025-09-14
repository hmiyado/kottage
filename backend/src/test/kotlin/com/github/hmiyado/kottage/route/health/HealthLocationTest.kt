package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.application.plugins.statuspages.OpenApiStatusPageRouter
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Health
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.service.health.HealthService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class HealthLocationTest : DescribeSpec() {
    @MockK
    lateinit var healthService: HealthService

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@HealthLocationTest)
    }

    private fun ApplicationTestBuilder.init() {
        application {
            contentNegotiation()
            routing {
                HealthLocation(healthService).addRoute(this)
            }
        }
    }

    init {
        describe("GET ${Paths.healthGet}") {
            it("should return OK") {
                testApplication {
                    init()
                    val expected = Health()
                    every { healthService.getHealth() } returns expected

                    val response = client.get(Paths.healthGet)
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }
        }
    }
}
