package com.github.hmiyado.kottage.route.health

import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.routing
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.helper.shouldMatchAsJson
import com.github.hmiyado.kottage.model.Health
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.service.health.HealthService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class HealthLocationTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    lateinit var healthService: HealthService

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this@HealthLocationTest)
        routing {
            HealthLocation(healthService).addRoute(this)
        }

        describe("GET ${Paths.healthGet}") {
            it("should return OK") {
                val expected = Health()
                every { healthService.getHealth() } returns expected
                get(Paths.healthGet).run {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldMatchAsJson expected
                }
            }
        }
    }
}
