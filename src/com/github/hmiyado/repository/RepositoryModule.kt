package com.github.hmiyado.repository

import com.github.hmiyado.application.configuration.DatabaseConfiguration
import com.github.hmiyado.repository.entries.Entries
import com.github.hmiyado.repository.entries.EntryRepositoryDatabase
import com.github.hmiyado.repository.entries.EntryRepositoryOnMemory
import org.koin.dsl.module

val repositoryModule = module {
    single {
        when (get<DatabaseConfiguration>()) {
            DatabaseConfiguration.Memory -> EntryRepositoryOnMemory()
            is DatabaseConfiguration.Postgres -> EntryRepositoryDatabase(Entries)
        }
    }
}
