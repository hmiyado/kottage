package infra.graphql

import com.github.hmiyado.graphqlModule
import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import graphql.GraphQL
import io.kotlintest.shouldBe
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.ZonedDateTime

class GraphqlKtTest : KoinTest {

    private val graphql: GraphQL by inject()

    @Test
    fun `graphql should be able to fetch articles`() {
        startKoin {
            modules(repositoryModule, graphqlModule)
        }

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

    private class ArticleRepositoryMock : ArticleRepository {
        override fun getArticles(): List<Article> {
            return (0..10).map { index ->
                Article(
                    "title $index",
                    "body $index",
                    ZonedDateTime.now()
                )
            }.toMutableList()
        }
    }

    companion object {
        private val repositoryModule = module {
            single { ArticleRepositoryMock() } bind ArticleRepository::class
        }
    }
}
