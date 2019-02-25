package com.github.hmiyado.repository

import com.github.hmiyado.model.Article

interface ArticleRepository {
    fun getArticles(): List<Article>
}