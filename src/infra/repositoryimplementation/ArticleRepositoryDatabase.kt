package com.github.hmiyado.infra.repositoryimplementation

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import com.github.hmiyado.repository.dao.Articles
import java.time.ZoneId
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
}
