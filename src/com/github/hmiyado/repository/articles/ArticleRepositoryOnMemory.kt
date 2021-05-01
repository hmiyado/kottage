package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article
import java.time.ZonedDateTime

class ArticleRepositoryOnMemory : ArticleRepository {
    private val articles = (0..10)
        .map { Article("title $it", "body $it", ZonedDateTime.now()) }
        .toMutableList()

    override fun getArticles(): List<Article> {
        return articles
    }

    override fun createArticle(title: String, body: String): Article {
        val article = Article(title, body, ZonedDateTime.now())
        articles += article
        return article
    }
}
