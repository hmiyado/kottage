package com.github.hmiyado.service

import com.github.hmiyado.service.entries.EntriesService
import com.github.hmiyado.service.entries.EntriesServiceImpl
import com.github.hmiyado.service.users.UsersService
import com.github.hmiyado.service.users.UsersServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    single { EntriesServiceImpl(get()) } bind EntriesService::class
    single<UsersService> { UsersServiceImpl(get()) }
}
