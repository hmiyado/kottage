package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.post
import com.github.hmiyado.kottage.helper.routing
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.post
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class HeaderCsrfProviderTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate() {
    @MockK
    lateinit var onFailFunction: () -> Unit

    override fun listeners(): List<TestListener> {
        return listOf(listener)
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        clearAllMocks()
    }

    init {
        MockKAnnotations.init(this)
        routing {
            post("/") { call.respond(HttpStatusCode.OK) }
        }
        install(Csrf) {
            requestFilter { httpMethod, _ -> listOf(HttpMethod.Post).contains(httpMethod) }
            header {
                validator { headers -> headers.contains(CustomHeaders.XCSRFToken) }
                onFail {
                    onFailFunction()
                    respond(HttpStatusCode.Forbidden)
                }
            }
        }

        describe("HeaderCsrf") {
            it("should succeed with valid csrf header") {
                post("/") {
                    addHeader("X-CSRF-Token", "")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
            it("should fail without valid csrf header") {
                every { onFailFunction.invoke() } just Runs
                post("/").run {
                    response shouldHaveStatus HttpStatusCode.Forbidden
                    verify { onFailFunction.invoke() }
                }
            }
        }
    }

}
