package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Entry
import com.github.hmiyado.repository.articles.ArticleRepository

interface ArticlesService {
    fun getArticles(): List<Entry>

    fun createArticle(title: String, body: String): Entry

    /**
     * find Article with [serialNumber].
     * return null if there is no article with specified [serialNumber].
     */
    fun getArticle(serialNumber: Long): Entry?

    /**
     * update Article with [serialNumber].
     * return updated Article.
     * return null if there is no article with specified [serialNumber].
     */
    fun updateArticle(serialNumber: Long, title: String?, body: String?): Entry?

    fun deleteArticle(serialNumber: Long)
}

class ArticlesServiceImpl(
    private val articleRepository: ArticleRepository
) : ArticlesService {
    override fun getArticles(): List<Entry> {
        return articleRepository.getArticles()
    }

    override fun createArticle(title: String, body: String): Entry {
        return articleRepository.createArticle(title, body)
    }

    override fun getArticle(serialNumber: Long): Entry? {
        return articleRepository.getArticle(serialNumber)
    }

    override fun updateArticle(serialNumber: Long, title: String?, body: String?): Entry? {
        return articleRepository.updateArticle(serialNumber, title, body)
    }

    override fun deleteArticle(serialNumber: Long) {
        articleRepository.deleteArticle(serialNumber)
    }
}
