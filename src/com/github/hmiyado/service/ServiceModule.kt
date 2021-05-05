package com.github.hmiyado.service

import com.github.hmiyado.service.entries.EntriesService
import com.github.hmiyado.service.entries.EntriesServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    single { EntriesServiceImpl(get()) } bind EntriesService::class
}
