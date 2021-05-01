package com.github.hmiyado.service

import com.github.hmiyado.service.articles.ArticlesService
import com.github.hmiyado.service.articles.ArticlesServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    single { ArticlesServiceImpl(get()) } bind ArticlesService::class
}
