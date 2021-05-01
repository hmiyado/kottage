package com.github.hmiyado.repository

import com.github.hmiyado.repository.articles.ArticleRepository
import com.github.hmiyado.repository.articles.ArticleRepositoryDatabase
import com.github.hmiyado.repository.articles.ArticleRepositoryOnMemory
import com.github.hmiyado.repository.articles.Articles
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    val database = initializeDatabase()
    when (database) {
        Database.Postgres -> {
            single { ArticleRepositoryDatabase(Articles) } bind ArticleRepository::class
        }
        Database.Memory -> {
            single { ArticleRepositoryOnMemory() } bind ArticleRepository::class
        }
    }
}
