package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.articles.ArticleRepository

interface ArticlesService {
    fun getArticles(): List<Article>

    fun createArticle(title: String, body: String): Article
}

class ArticlesServiceImpl(
    private val articleRepository: ArticleRepository
) : ArticlesService {
    override fun getArticles(): List<Article> {
        return articleRepository.getArticles()
    }

    override fun createArticle(title: String, body: String): Article {
        return articleRepository.createArticle(title, body)
    }
}
