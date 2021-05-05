package com.github.hmiyado.repository.entries

import com.github.hmiyado.model.Entry
import java.time.ZonedDateTime

class EntryRepositoryOnMemory : EntryRepository {
    private val entries = (0..10)
        .map { Entry(it.toLong(), "title $it", "body $it", ZonedDateTime.now()) }
        .toMutableList()

    override fun getEntries(): List<Entry> {
        return entries
    }

    override fun createEntry(title: String, body: String): Entry {
        val article = Entry((entries.size + 1).toLong(), title, body, ZonedDateTime.now())
        entries += article
        return article
    }

    override fun getEntry(serialNumber: Long): Entry? {
        return entries.find { it.serialNumber == serialNumber }
    }

    override fun updateEntry(serialNumber: Long, title: String?, body: String?): Entry? {
        val tmp = entries.map {
            if (it.serialNumber != serialNumber) {
                return@map it
            }
            return@map it.copy(title = title ?: it.title, body = body ?: it.body)
        }
        entries.removeAll { true }
        entries.addAll(tmp)
        return entries.find { it.serialNumber == serialNumber }
    }

    override fun deleteEntry(serialNumber: Long) {
        entries.removeIf { it.serialNumber == serialNumber }
    }
}
