package com.github.hmiyado.infra.repositoryimplementation

import com.github.hmiyado.infra.db.Articles
import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import com.github.hmiyado.util.toZonedDateTime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZonedDateTime

class ArticleRepositoryDatabase(
    private val articles: Articles
) : ArticleRepository {
    override fun getArticles(): List<Article> {
        return transaction {
            articles.selectAll().map {
                Article(
                    it[Articles.title],
                    it[Articles.body],
                    ZonedDateTime.now()
                    // TODO
//                    it[Articles.dateTime].toZonedDateTime()
                )
            }
        }
    }
}