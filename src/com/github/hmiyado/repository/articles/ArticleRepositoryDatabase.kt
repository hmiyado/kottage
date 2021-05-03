package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article
import java.time.LocalDateTime
import java.time.ZoneId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ArticleRepositoryDatabase(
    private val articles: Articles
) : ArticleRepository {
    override fun getArticles(): List<Article> {
        return transaction {
            articles
                .selectAll()
                .map { it.toArticle() }
        }
    }

    override fun createArticle(title: String, body: String): Article {
        return transaction {
            val id = articles.insertAndGetId {
                it[Articles.title] = title
                it[Articles.body] = body
                it[dateTime] = LocalDateTime.now()
            }
            articles
                .select { articles.id eq id }
                .first()
                .toArticle()
        }
    }

    override fun getArticle(serialNumber: Long): Article? {
        return transaction {
            articles
                .select { articles.id eq serialNumber }
                .firstOrNull()
                ?.toArticle()
        }
    }

    override fun deleteArticle(serialNumber: Long) {
        transaction {
            articles.deleteWhere { articles.id eq serialNumber }
        }
    }

    private fun ResultRow.toArticle(): Article {
        return Article(
            get(Articles.id).value,
            get(Articles.title),
            get(Articles.body),
            get(Articles.dateTime).atZone(ZoneId.systemDefault())
        )
    }
}
