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
     * update an [Entry] with [serialNumber] and wrote by an user with [userId].
     * return updated [Entry].
     * return null if there is no entry with specified [serialNumber].
     */
    @Throws(NoSuchEntryException::class, ForbiddenOperationException::class)
    fun updateEntry(serialNumber: Long, userId: Long, title: String?, body: String?): Entry

    @Throws(ForbiddenOperationException::class)
    fun deleteEntry(serialNumber: Long, userId: Long)

    data class NoSuchEntryException(val serialNumber: Long) :
        IllegalStateException("No entry with serialNumber: $serialNumber")

    data class ForbiddenOperationException(val serialNumber: Long, val userId: Long) :
        IllegalStateException("user $userId cannot operate entry $serialNumber")
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

    override fun updateEntry(serialNumber: Long, userId: Long, title: String?, body: String?): Entry {
        val updatedEntry = entryRepository.updateEntry(serialNumber, userId, title, body)
        if (updatedEntry == null) {
            val entry = entryRepository.getEntry(serialNumber)
            if (entry == null) {
                throw EntriesService.NoSuchEntryException(serialNumber)
            } else {
                throw EntriesService.ForbiddenOperationException(serialNumber, userId)
            }
        }
        return updatedEntry
    }

    override fun deleteEntry(serialNumber: Long, userId: Long) {
        val entry = entryRepository.getEntry(serialNumber) ?: return
        if (entry.author.id != userId) {
            throw EntriesService.ForbiddenOperationException(serialNumber, userId)
        }
        entryRepository.deleteEntry(serialNumber)
    }
}
