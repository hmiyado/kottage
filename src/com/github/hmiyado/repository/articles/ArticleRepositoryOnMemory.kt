package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article
import java.time.ZonedDateTime

class ArticleRepositoryOnMemory : ArticleRepository {
    override fun getArticles(): List<Article> {
        // return mock values until real database access implementation
        return (0..10)
            .map { Article("title $it", "body $it", ZonedDateTime.now()) }
    }
}
