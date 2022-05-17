package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.helper.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class RootLocationTest : DescribeSpec({
    describe("route ${RootLocation.path}") {
        it("should return Hello World!") {
            withTestApplication({
                routing {
                    RootLocation().addRoute(this)
                }
            }) {
                with(handleRequest(HttpMethod.Get, RootLocation.path)) {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.content shouldBe  "Hello World!"
                }
            }
        }
    }
})
