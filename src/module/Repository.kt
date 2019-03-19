package com.github.hmiyado.module

import com.github.hmiyado.infra.db.Articles
import com.github.hmiyado.infra.repositoryimplementation.ArticleRepositoryDatabase
import com.github.hmiyado.repository.ArticleRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { ArticleRepositoryDatabase(Articles) } bind ArticleRepository::class
}