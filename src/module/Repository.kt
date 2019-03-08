package com.github.hmiyado.module

import com.github.hmiyado.infra.repositoryimplementation.ArticleRepositoryImpl
import com.github.hmiyado.repository.ArticleRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { ArticleRepositoryImpl() } bind ArticleRepository::class
}