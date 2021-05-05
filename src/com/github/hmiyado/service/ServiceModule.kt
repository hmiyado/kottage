package com.github.hmiyado.service

import com.github.hmiyado.service.articles.EntriesService
import com.github.hmiyado.service.articles.EntriesServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    single { EntriesServiceImpl(get()) } bind EntriesService::class
}
