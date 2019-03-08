package com.github.hmiyado.infra.repositoryimplementation

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import java.time.ZonedDateTime

class ArticleRepositoryImpl : ArticleRepository {
    override fun getArticles(): List<Article> {
        // return mock values until real database access implementation
        return (0..10)
            .map { Article("title $it", "body $it", ZonedDateTime.now()) }
    }
}