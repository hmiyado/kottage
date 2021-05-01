package com.github.hmiyado.service

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.articles.ArticleRepository

interface ArticlesService {
    fun getArticles(): List<Article>
}

class ArticlesServiceImpl(
    private val articleRepository: ArticleRepository
) : ArticlesService {
    override fun getArticles(): List<Article> {
        return articleRepository.getArticles()
    }
}
