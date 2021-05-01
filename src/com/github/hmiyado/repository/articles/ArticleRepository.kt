package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Article

interface ArticleRepository {
    fun getArticles(): List<Article>

    fun createArticle(title: String, body: String): Article
}
