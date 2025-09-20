package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.repository.entries.EntryRepository
import kotlin.math.min

interface EntriesService {
    fun getEntries(
        limit: Long? = null,
        offset: Long? = null,
    ): Page<Entry>

    fun createEntry(
        title: String,
        body: String,
        userId: Long,
    ): Entry

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
    fun updateEntry(
        serialNumber: Long,
        userId: Long,
        title: String?,
        body: String?,
    ): Entry

    @Throws(ForbiddenOperationException::class)
    fun deleteEntry(
        serialNumber: Long,
        userId: Long,
    )

    data class NoSuchEntryException(
        val serialNumber: Long,
    ) : NoSuchElementException("No entry with serialNumber: $serialNumber")

    data class ForbiddenOperationException(
        val serialNumber: Long,
        val userId: Long,
    ) : IllegalStateException("user $userId cannot operate entry $serialNumber")

    companion object {
        const val MAX_LIMIT = 100L
        const val DEFAULT_LIMIT = 20L
        const val DEFAULT_OFFSET = 0L
    }
}

class EntriesServiceImpl(
    private val entryRepository: EntryRepository,
) : EntriesService {
    override fun getEntries(
        limit: Long?,
        offset: Long?,
    ): Page<Entry> {
        val actualLimit = min(limit ?: EntriesService.DEFAULT_LIMIT, EntriesService.MAX_LIMIT)
        val actualOffset = offset ?: EntriesService.DEFAULT_OFFSET
        val entries = entryRepository.getEntries(actualLimit, actualOffset)
        return Page(
            totalCount = entryRepository.getEntryTotalCount(),
            items = entries,
            limit = actualLimit,
            offset = actualOffset,
        )
    }

    override fun createEntry(
        title: String,
        body: String,
        userId: Long,
    ): Entry = entryRepository.createEntry(title, body, userId)

    override fun getEntry(serialNumber: Long): Entry? = entryRepository.getEntry(serialNumber)

    override fun updateEntry(
        serialNumber: Long,
        userId: Long,
        title: String?,
        body: String?,
    ): Entry {
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

    override fun deleteEntry(
        serialNumber: Long,
        userId: Long,
    ) {
        val entry = entryRepository.getEntry(serialNumber) ?: return
        if (entry.author.id != userId) {
            throw EntriesService.ForbiddenOperationException(serialNumber, userId)
        }
        entryRepository.deleteEntry(serialNumber)
    }
}
