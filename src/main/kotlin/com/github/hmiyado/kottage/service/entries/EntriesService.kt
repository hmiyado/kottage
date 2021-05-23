package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.repository.entries.EntryRepository

interface EntriesService {
    fun getEntries(): List<Entry>

    fun createEntry(title: String, body: String, userId: Long): Entry

    /**
     * find an [Entry] with [serialNumber].
     * return null if there is no entry with specified [serialNumber].
     */
    fun getEntry(serialNumber: Long): Entry?

    /**
     * update an [Entry] with [serialNumber].
     * return updated [Entry].
     * return null if there is no entry with specified [serialNumber].
     */
    fun updateEntry(serialNumber: Long, title: String?, body: String?): Entry?

    fun deleteEntry(serialNumber: Long)
}

class EntriesServiceImpl(
    private val entryRepository: EntryRepository
) : EntriesService {
    override fun getEntries(): List<Entry> {
        return entryRepository.getEntries()
    }

    override fun createEntry(title: String, body: String, userId: Long): Entry {
        return entryRepository.createEntry(title, body, userId)
    }

    override fun getEntry(serialNumber: Long): Entry? {
        return entryRepository.getEntry(serialNumber)
    }

    override fun updateEntry(serialNumber: Long, title: String?, body: String?): Entry? {
        return entryRepository.updateEntry(serialNumber, title, body)
    }

    override fun deleteEntry(serialNumber: Long) {
        entryRepository.deleteEntry(serialNumber)
    }
}
