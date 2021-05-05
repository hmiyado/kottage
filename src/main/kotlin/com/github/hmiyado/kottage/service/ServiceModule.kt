package com.github.hmiyado.kottage.service

import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.entries.EntriesServiceImpl
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.UsersServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    single { EntriesServiceImpl(get()) } bind EntriesService::class
    single<UsersService> { UsersServiceImpl(get()) }
}
