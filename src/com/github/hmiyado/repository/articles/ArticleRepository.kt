package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article

interface ArticleRepository {
    fun getArticles(): List<Article>

    fun createArticle(title: String, body: String): Article

    fun getArticle(serialNumber: Long): Article?

    fun deleteArticle(serialNumber: Long)
}
