package com.github.hmiyado.repository.entries

import com.github.hmiyado.model.Entry

interface EntryRepository {
    fun getEntries(): List<Entry>

    fun createEntry(title: String, body: String): Entry

    fun getEntry(serialNumber: Long): Entry?

    fun updateEntry(serialNumber: Long, title: String? = null, body: String? = null): Entry?

    fun deleteEntry(serialNumber: Long)
}
