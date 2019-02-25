package com.github.hmiyado.infra.graphql

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.ArticleRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class ArticlesFetcher(private val articleRepository: ArticleRepository) : DataFetcher<List<Article>> {
    override fun get(environment: DataFetchingEnvironment?): List<Article> {
        return articleRepository.getArticles()
    }
}