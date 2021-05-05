package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Entry
import com.github.hmiyado.repository.articles.EntryRepository

interface EntriesService {
    fun getEntries(): List<Entry>

    fun createEntry(title: String, body: String): Entry

    /**
     * find Article with [serialNumber].
     * return null if there is no article with specified [serialNumber].
     */
    fun getEntry(serialNumber: Long): Entry?

    /**
     * update Article with [serialNumber].
     * return updated Article.
     * return null if there is no article with specified [serialNumber].
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

    override fun createEntry(title: String, body: String): Entry {
        return entryRepository.createEntry(title, body)
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
