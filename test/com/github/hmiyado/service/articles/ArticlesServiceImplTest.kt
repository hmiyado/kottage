package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.articles.ArticleRepository
import io.kotlintest.Spec
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import java.time.ZonedDateTime

class ArticlesServiceImplTest : DescribeSpec() {
    private var mockRepository = object : ArticleRepository {
        val mockArticles = mutableListOf<Article>()
        var createArticleTime = ZonedDateTime.now()
        override fun getArticles(): List<Article> {
            return mockArticles
        }

        override fun createArticle(title: String, body: String): Article {
            val article = Article(title, body, createArticleTime)
            mockArticles += article
            return article
        }

    }
    private lateinit var service: ArticlesService
    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        service = ArticlesServiceImpl(mockRepository)
    }

    init {

        describe("getArticles") {
            val dateTime = ZonedDateTime.now()
            mockRepository.mockArticles.add(Article("title 1", "body 1", dateTime))
            service.getArticles() shouldBe mockRepository.mockArticles
        }

        describe("createArticle") {
            val dateTime = ZonedDateTime.now()
            mockRepository.createArticleTime = dateTime
            val createdArticle = service.createArticle("title 1", "body 1")
            createdArticle shouldBe Article("title 1", "body 1", dateTime)
        }
    }

}
