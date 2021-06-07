package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Entry

interface EntryRepository {
    fun getEntries(): List<Entry>

    fun createEntry(title: String, body: String, userId: Long): Entry

    fun getEntry(serialNumber: Long): Entry?

    /**
     * update an entry.
     * its has [serialNumber] and wrote by user with [userId].
     * following field can be updated.
     * - [title]
     * - [body]
     */
    fun updateEntry(serialNumber: Long, userId: Long, title: String? = null, body: String? = null): Entry?

    fun deleteEntry(serialNumber: Long)
}
