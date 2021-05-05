package com.github.hmiyado.kottage.route

import io.kotest.assertions.ktor.shouldHaveContent
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class HelloWorldRoutingTest : DescribeSpec({
    describe("route /") {
        it("should return Hello World!") {
            withTestApplication({
                routing {
                    helloWorld()
                }
            }) {
                with(handleRequest(HttpMethod.Get, "/")) {
                    response shouldHaveStatus HttpStatusCode.OK
                    response shouldHaveContent "Hello World!"
                }
            }
        }
    }
})
