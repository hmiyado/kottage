package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Entry

interface ArticleRepository {
    fun getArticles(): List<Entry>

    fun createArticle(title: String, body: String): Entry

    fun getArticle(serialNumber: Long): Entry?

    fun updateArticle(serialNumber: Long, title: String? = null, body: String? = null): Entry?

    fun deleteArticle(serialNumber: Long)
}
