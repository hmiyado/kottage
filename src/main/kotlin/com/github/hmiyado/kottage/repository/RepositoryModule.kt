package com.github.hmiyado.kottage.repository

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import com.github.hmiyado.kottage.repository.entries.EntryRepositoryDatabase
import com.github.hmiyado.kottage.repository.entries.EntryRepositoryOnMemory
import com.github.hmiyado.kottage.repository.users.UserRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.UserRepositoryMemory
import com.github.hmiyado.kottage.repository.users.admins.AdminRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.admins.AdminRepositoryMemory
import org.koin.dsl.module

val repositoryModule = module {
    single {
        when (get<DatabaseConfiguration>()) {
            DatabaseConfiguration.Memory -> EntryRepositoryOnMemory()
            is DatabaseConfiguration.Postgres -> EntryRepositoryDatabase()
            is DatabaseConfiguration.MySql -> EntryRepositoryDatabase()
        }
    }
    single {
        when (get<DatabaseConfiguration>()) {
            DatabaseConfiguration.Memory -> UserRepositoryMemory()
            is DatabaseConfiguration.Postgres -> UserRepositoryDatabase()
            is DatabaseConfiguration.MySql -> UserRepositoryDatabase()
        }
    }
    single {
        when (get<DatabaseConfiguration>()) {
            DatabaseConfiguration.Memory -> AdminRepositoryMemory()
            is DatabaseConfiguration.MySql -> AdminRepositoryDatabase()
            is DatabaseConfiguration.Postgres -> AdminRepositoryDatabase()
        }
    }
}
