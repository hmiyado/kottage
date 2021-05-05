package com.github.hmiyado.repository.articles

import com.github.hmiyado.model.Entry
import java.time.LocalDateTime
import java.time.ZoneId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class ArticleRepositoryDatabase(
    private val entries: Entries
) : ArticleRepository {
    override fun getArticles(): List<Entry> {
        return transaction {
            entries
                .selectAll()
                .map { it.toArticle() }
        }
    }

    override fun createArticle(title: String, body: String): Entry {
        return transaction {
            val id = entries.insertAndGetId {
                it[Entries.title] = title
                it[Entries.body] = body
                it[dateTime] = LocalDateTime.now()
            }
            entries
                .select { entries.id eq id }
                .first()
                .toArticle()
        }
    }

    override fun getArticle(serialNumber: Long): Entry? {
        return transaction {
            entries
                .select { entries.id eq serialNumber }
                .firstOrNull()
                ?.toArticle()
        }
    }

    override fun updateArticle(serialNumber: Long, title: String?, body: String?): Entry? {
        return transaction {
            entries.update({ Entries.id eq serialNumber }) { willUpdate ->
                title?.let {
                    willUpdate[Entries.title] = it
                }
                body?.let {
                    willUpdate[Entries.body] = it
                }
            }
            entries.select { Entries.id eq serialNumber }.firstOrNull()?.toArticle()
        }
    }

    override fun deleteArticle(serialNumber: Long) {
        transaction {
            entries.deleteWhere { entries.id eq serialNumber }
        }
    }

    private fun ResultRow.toArticle(): Entry {
        return Entry(
            get(Entries.id).value,
            get(Entries.title),
            get(Entries.body),
            get(Entries.dateTime).atZone(ZoneId.of("Asia/Tokyo"))
        )
    }
}
