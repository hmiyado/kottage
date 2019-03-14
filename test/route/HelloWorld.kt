package route

import com.github.hmiyado.route.helloWorld
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class HelloWorld : DescribeSpec({
    describe("route /") {
        it("should return Hello World!") {
            withTestApplication({
                routing {
                    helloWorld()
                }
            }) {
                with(handleRequest(HttpMethod.Get, "/")) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe "Hello World!"
                }
            }
        }
    }
})