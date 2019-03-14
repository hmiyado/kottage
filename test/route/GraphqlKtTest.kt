package route

import com.github.hmiyado.route.graphql
import com.google.gson.Gson
import graphql.GraphQL
import graphql.schema.StaticDataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.koin.dsl.module
import org.koin.ktor.ext.installKoin

class GraphqlKtTest : DescribeSpec({
    describe("route /graphql") {
        it("should return result of graphql when post") {
            withTestApplication({
                installKoin { modules(testGraphqlModule) }
                routing {
                    graphql()
                }
            }) {
                with(
                    handleRequest(HttpMethod.Post, "/graphql") {
                        setBody(
                            """
                            query {
                                graph
                            }
                        """.trimIndent()
                        )
                    }) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content.let { Gson().fromJson(it, Map::class.java) } shouldBe """
                        {
                            "data": {
                                "graph": "ql"
                            }
                        }
                    """.trimIndent().let { Gson().fromJson(it, Map::class.java) }
                }
            }
        }
    }
})

private val testGraphqlSchema = """
schema {
    query: Query
}

type Query {
    graph: String!
}
""".trimIndent()

private val testGraphqlModule = module {
    single { SchemaParser() }
    single { get<SchemaParser>().parse(testGraphqlSchema) }
    single { SchemaGenerator() }
    single { get<SchemaGenerator>().makeExecutableSchema(get(), get()) }
    single {
        RuntimeWiring
            .newRuntimeWiring()
            .type("Query") {
                it.dataFetcher("graph", StaticDataFetcher("ql"))
            }
            .build()
    }
    single { GraphQL.newGraphQL(get()).build() }
}