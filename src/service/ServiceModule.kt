package com.github.hmiyado.module

import com.github.hmiyado.service.ArticlesService
import com.github.hmiyado.service.ArticlesServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    single { ArticlesServiceImpl(get()) } bind ArticlesService::class
}
