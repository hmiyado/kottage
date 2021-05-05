package com.github.hmiyado.service.articles

import com.github.hmiyado.model.Entry
import com.github.hmiyado.repository.articles.EntryRepository

interface ArticlesService {
    fun getArticles(): List<Entry>

    fun createArticle(title: String, body: String): Entry

    /**
     * find Article with [serialNumber].
     * return null if there is no article with specified [serialNumber].
     */
    fun getArticle(serialNumber: Long): Entry?

    /**
     * update Article with [serialNumber].
     * return updated Article.
     * return null if there is no article with specified [serialNumber].
     */
    fun updateArticle(serialNumber: Long, title: String?, body: String?): Entry?

    fun deleteArticle(serialNumber: Long)
}

class ArticlesServiceImpl(
    private val entryRepository: EntryRepository
) : ArticlesService {
    override fun getArticles(): List<Entry> {
        return entryRepository.getEntries()
    }

    override fun createArticle(title: String, body: String): Entry {
        return entryRepository.createEntry(title, body)
    }

    override fun getArticle(serialNumber: Long): Entry? {
        return entryRepository.getEntry(serialNumber)
    }

    override fun updateArticle(serialNumber: Long, title: String?, body: String?): Entry? {
        return entryRepository.updateEntry(serialNumber, title, body)
    }

    override fun deleteArticle(serialNumber: Long) {
        entryRepository.deleteEntry(serialNumber)
    }
}
