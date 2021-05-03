package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.articles.ArticleRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK

class ArticlesServiceImplTest : DescribeSpec() {
    @MockK
    lateinit var articleRepository: ArticleRepository
    private lateinit var service: ArticlesService

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this, relaxUnitFun = true)
        service = ArticlesServiceImpl(articleRepository)
    }

    init {

        describe("getArticles") {
            val articles = listOf(Article(1, "title 1", "body 1"))
            every { articleRepository.getArticles() } returns articles
            service.getArticles() shouldBe articles
        }

        describe("createArticle") {
            val article = Article(1, "title 1", "body 1")
            every { articleRepository.createArticle(any(), any()) } returns article
            val createdArticle = service.createArticle("title 1", "body 1")
            createdArticle shouldBe article
        }
    }

}
