package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Article
import com.github.hmiyado.repository.articles.ArticleRepository

interface ArticlesService {
    fun getArticles(): List<Article>

    fun createArticle(title: String, body: String): Article

    /**
     * find Article with [serialNumber].
     * return null if there is no article with specified [serialNumber].
     */
    fun getArticle(serialNumber: Long): Article?

    fun deleteArticle(serialNumber: Long)
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

    override fun getArticle(serialNumber: Long): Article? {
        return articleRepository.getArticle(serialNumber)
    }

    override fun deleteArticle(serialNumber: Long) {
        TODO("Not yet implemented")
    }
}
