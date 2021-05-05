package com.github.hmiyado.repository

import com.github.hmiyado.application.configuration.DatabaseConfiguration
import com.github.hmiyado.repository.articles.ArticleRepositoryDatabase
import com.github.hmiyado.repository.articles.ArticleRepositoryOnMemory
import com.github.hmiyado.repository.articles.Entries
import org.koin.dsl.module

val repositoryModule = module {
    single {
        when (get<DatabaseConfiguration>()) {
            DatabaseConfiguration.Memory -> ArticleRepositoryOnMemory()
            is DatabaseConfiguration.Postgres -> ArticleRepositoryDatabase(Entries)
        }
    }
}
