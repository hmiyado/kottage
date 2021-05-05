package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Entry

interface EntryRepository {
    fun getEntries(): List<Entry>

    fun createEntry(title: String, body: String): Entry

    fun getEntry(serialNumber: Long): Entry?

    fun updateEntry(serialNumber: Long, title: String? = null, body: String? = null): Entry?

    fun deleteEntry(serialNumber: Long)
}
