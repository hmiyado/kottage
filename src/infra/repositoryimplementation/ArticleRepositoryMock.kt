package com.github.hmiyado.infra.repositoryimplementation

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import java.time.ZonedDateTime

class ArticleRepositoryMock : ArticleRepository {
    override fun getArticles(): List<Article> {
        return (0..10).map { index ->
            Article(
                "title $index",
                "body $index",
                ZonedDateTime.now()
            )
        }.toMutableList()
    }
}