package com.github.hmiyado.infra.repositoryimplementation

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository

class ArticleRepositoryImpl : ArticleRepository {
    override fun getArticles(): List<Article> {
        return emptyList()
    }
}