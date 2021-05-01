package com.github.hmiyado.route

import com.github.hmiyado.repository.repositoryModule
import com.github.hmiyado.service.serviceModule
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.koin.core.context.startKoin

class ArticlesRoutingKtTest : DescribeSpec({
    describe("com.github.hmiyado.route /articles") {
        it("should return articles") {
            startKoin {
                modules(repositoryModule, serviceModule)
            }
            withTestApplication({
                routing {
                    articles()
                }
            }) {
                with(handleRequest(HttpMethod.Get, "/articles")) {
                    response.status() shouldBe HttpStatusCode.OK
                }
            }
        }
    }
})
