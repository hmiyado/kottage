import com.github.hmiyado.graphql
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec

internal class GraphqlKtTest : DescribeSpec({
    describe("graphql") {
        it("should be able to fetch articles") {
            val query = """
                {
                    articles {
                        title
                        body
                    }
                }
            """.trimIndent()
            val actual: Map<String, List<Map<String, String>>> = graphql.execute(query).getData()
            val articles = actual.getValue("articles")
            articles.forEachIndexed { index, article ->
                article.getValue("title") shouldBe "title $index"
                article.getValue("body") shouldBe "body $index"
            }
        }
    }
})
