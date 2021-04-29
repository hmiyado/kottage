package com.github.hmiyado.infra.repositoryimplementation

import com.github.hmiyado.infra.db.Articles
import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneId

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
}