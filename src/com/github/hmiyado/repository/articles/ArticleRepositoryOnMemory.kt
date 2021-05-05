package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Entry
import java.time.ZonedDateTime

class ArticleRepositoryOnMemory : ArticleRepository {
    private val articles = (0..10)
        .map { Entry(it.toLong(), "title $it", "body $it", ZonedDateTime.now()) }
        .toMutableList()

    override fun getArticles(): List<Entry> {
        return articles
    }

    override fun createArticle(title: String, body: String): Entry {
        val article = Entry((articles.size + 1).toLong(), title, body, ZonedDateTime.now())
        articles += article
        return article
    }

    override fun getArticle(serialNumber: Long): Entry? {
        return articles.find { it.serialNumber == serialNumber }
    }

    override fun updateArticle(serialNumber: Long, title: String?, body: String?): Entry? {
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
