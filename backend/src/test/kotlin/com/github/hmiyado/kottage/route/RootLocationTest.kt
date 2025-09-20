package com.github.hmiyado.kottage.route

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication

class RootLocationTest :
    DescribeSpec({
        describe("route ${RootLocation.PATH}") {
            it("should return Hello World!") {
                testApplication {
                    routing {
                        RootLocation().addRoute(this)
                    }
                    val response = client.get(RootLocation.PATH)
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "Hello World!"
                }
            }
        }
    })
