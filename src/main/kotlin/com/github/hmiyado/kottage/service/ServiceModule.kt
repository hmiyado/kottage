package com.github.hmiyado.kottage.service

import com.github.hmiyado.kottage.service.entries.EntriesCommentsService
import com.github.hmiyado.kottage.service.entries.EntriesCommentsServiceImpl
import com.github.hmiyado.kottage.service.entries.EntriesService
import com.github.hmiyado.kottage.service.entries.EntriesServiceImpl
import com.github.hmiyado.kottage.service.health.HealthService
import com.github.hmiyado.kottage.service.health.HealthServiceImpl
import com.github.hmiyado.kottage.service.users.PasswordGenerator
import com.github.hmiyado.kottage.service.users.SaltGenerator
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.UsersServiceImpl
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import com.github.hmiyado.kottage.service.users.admins.AdminsServiceImpl
import kotlin.random.Random
import org.koin.dsl.module

val serviceModule = module {
    single<EntriesService> { EntriesServiceImpl(get()) }
    single<EntriesCommentsService> { EntriesCommentsServiceImpl(get()) }
    single<Random> { Random.Default }
    single { SaltGenerator(get()) }
    single { PasswordGenerator }
    single<UsersService> { UsersServiceImpl(get(), get(), get()) }
    single<AdminsService> { AdminsServiceImpl(get()) }
    single<HealthService> { HealthServiceImpl(get(), get()) }
}
