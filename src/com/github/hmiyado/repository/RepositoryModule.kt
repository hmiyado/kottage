package com.github.hmiyado.repository

import com.github.hmiyado.application.DatabaseConfiguration
import com.github.hmiyado.repository.articles.ArticleRepository
import com.github.hmiyado.repository.articles.ArticleRepositoryDatabase
import com.github.hmiyado.repository.articles.ArticleRepositoryOnMemory
import com.github.hmiyado.repository.articles.Articles
import org.koin.dsl.bind
import org.koin.dsl.module

fun provideRepositoryModule(databaseConfiguration: DatabaseConfiguration) = module {
    initializeDatabase(databaseConfiguration)
    when (databaseConfiguration) {
        DatabaseConfiguration.Memory -> {
            single { ArticleRepositoryOnMemory() } bind ArticleRepository::class
        }
        is DatabaseConfiguration.Postgres -> {
            single { ArticleRepositoryDatabase(Articles) } bind ArticleRepository::class
        }
    }
}
