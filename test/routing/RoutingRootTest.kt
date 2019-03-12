package routing

import com.github.hmiyado.routing
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class RoutingRootTest : DescribeSpec({
    describe("routing /") {
        it("should return Hello World!") {
            withTestApplication({
                routing()
            }) {
                with(handleRequest(HttpMethod.Get, "/")) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe "Hello World!"
                }
            }
        }
    }
})