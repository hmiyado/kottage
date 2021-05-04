package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article
import java.time.ZonedDateTime

class ArticleRepositoryOnMemory : ArticleRepository {
    private val articles = (0..10)
        .map { Article(it.toLong(), "title $it", "body $it", ZonedDateTime.now()) }
        .toMutableList()

    override fun getArticles(): List<Article> {
        return articles
    }

    override fun createArticle(title: String, body: String): Article {
        val article = Article((articles.size + 1).toLong(), title, body, ZonedDateTime.now())
        articles += article
        return article
    }

    override fun getArticle(serialNumber: Long): Article? {
        return articles.find { it.serialNumber == serialNumber }
    }

    override fun updateArticle(serialNumber: Long, title: String?, body: String?): Article? {
        val tmp = articles.map {
            if (it.serialNumber != serialNumber) {
                return@map it
            }
            return@map it.copy(title = title ?: it.title, body = body ?: it.body)
        }
        articles.removeAll { true }
        articles.addAll(tmp)
        return articles.find { it.serialNumber == serialNumber }
    }

    override fun deleteArticle(serialNumber: Long) {
        articles.removeIf { it.serialNumber == serialNumber }
    }
}
