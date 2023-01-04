package com.github.hmiyado.kottage.repository

import com.github.hmiyado.kottage.application.configuration.DatabaseConfiguration
import com.github.hmiyado.kottage.repository.entries.EntryCommentRepositoryDatabase
import com.github.hmiyado.kottage.repository.entries.EntryCommentRepositoryMemory
import com.github.hmiyado.kottage.repository.entries.EntryRepositoryDatabase
import com.github.hmiyado.kottage.repository.entries.EntryRepositoryOnMemory
import com.github.hmiyado.kottage.repository.oauth.OauthGoogleRepository
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
            DatabaseConfiguration.Memory -> EntryCommentRepositoryMemory()
            is DatabaseConfiguration.Postgres -> EntryCommentRepositoryDatabase()
            is DatabaseConfiguration.MySql -> EntryCommentRepositoryDatabase()
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
    single { OauthGoogleRepository(get()) }
}
