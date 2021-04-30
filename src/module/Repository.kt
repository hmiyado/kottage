package com.github.hmiyado.module

import com.github.hmiyado.Database
import com.github.hmiyado.infra.db.Articles
import com.github.hmiyado.infra.repositoryimplementation.ArticleRepositoryDatabase
import com.github.hmiyado.infra.repositoryimplementation.ArticleRepositoryOnMemory
import com.github.hmiyado.initializeDatabase
import com.github.hmiyado.repository.ArticleRepository
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
