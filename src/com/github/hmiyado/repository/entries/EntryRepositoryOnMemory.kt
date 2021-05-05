package com.github.hmiyado.repository.entries

import com.github.hmiyado.model.Entry
import java.time.ZonedDateTime

class EntryRepositoryOnMemory : EntryRepository {
    private val articles = (0..10)
        .map { Entry(it.toLong(), "title $it", "body $it", ZonedDateTime.now()) }
        .toMutableList()

    override fun getEntries(): List<Entry> {
        return articles
    }

    override fun createEntry(title: String, body: String): Entry {
        val article = Entry((articles.size + 1).toLong(), title, body, ZonedDateTime.now())
        articles += article
        return article
    }

    override fun getEntry(serialNumber: Long): Entry? {
        return articles.find { it.serialNumber == serialNumber }
    }

    override fun updateEntry(serialNumber: Long, title: String?, body: String?): Entry? {
        val tmp = articles.map {
            if (it.serialNumber != serialNumber) {
                return@map it
            }
            return@map it.copy(title = title ?: it.title, body = body ?: it.body)
        }
        articles.removeAll { true }
        articles.addAll(tmp)
        return articles.find { it.serialNumber == serialNumber }
    }

    override fun deleteEntry(serialNumber: Long) {
        articles.removeIf { it.serialNumber == serialNumber }
    }
}
