package route

import com.github.hmiyado.initializeDatabase
import com.github.hmiyado.module.repositoryModule
import com.github.hmiyado.module.serviceModule
import com.github.hmiyado.route.articles
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.koin.core.context.startKoin

class ArticlesRoutingKtTest : DescribeSpec({
    describe("route /articles") {
        it("should return articles") {
            startKoin {
                modules(repositoryModule, serviceModule)
            }
            withTestApplication({
                initializeDatabase()
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
