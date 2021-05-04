package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article

interface ArticleRepository {
    fun getArticles(): List<Article>

    fun createArticle(title: String, body: String): Article

    fun getArticle(serialNumber: Long): Article?

    fun updateArticle(serialNumber: Long, title: String? = null, body: String? = null): Article?

    fun deleteArticle(serialNumber: Long)
}
