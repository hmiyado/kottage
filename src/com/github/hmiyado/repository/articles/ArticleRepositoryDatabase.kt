package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article
import java.time.LocalDateTime
import java.time.ZoneId
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ArticleRepositoryDatabase(
    private val articles: Articles
) : ArticleRepository {
    override fun getArticles(): List<Article> {
        return transaction {
            articles.selectAll().map {
                Article(
                    it[Articles.title],
                    it[Articles.body],
                    it[Articles.dateTime].atZone(ZoneId.systemDefault())
                )
            }
        }
    }

    override fun createArticle(title: String, body: String): Article {
        return transaction {
            val id = articles.insertAndGetId {
                it[Articles.title] = title
                it[Articles.body] = body
                it[dateTime] = LocalDateTime.now()
            }
            articles.select { articles.id eq id }.first().let {
                Article(
                    it[Articles.title],
                    it[Articles.body],
                    it[Articles.dateTime].atZone(ZoneId.systemDefault())
                )
            }
        }
    }
}
