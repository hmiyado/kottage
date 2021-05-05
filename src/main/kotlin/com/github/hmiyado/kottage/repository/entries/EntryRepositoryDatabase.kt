package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Entry
import java.time.LocalDateTime
import java.time.ZoneId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class EntryRepositoryDatabase(
    private val entries: Entries
) : EntryRepository {
    override fun getEntries(): List<Entry> {
        return transaction {
            entries
                .selectAll()
                .map { it.toEntry() }
        }
    }

    override fun createEntry(title: String, body: String): Entry {
        return transaction {
            val id = entries.insertAndGetId {
                it[Entries.title] = title
                it[Entries.body] = body
                it[dateTime] = LocalDateTime.now()
            }
            entries
                .select { entries.id eq id }
                .first()
                .toEntry()
        }
    }

    override fun getEntry(serialNumber: Long): Entry? {
        return transaction {
            entries
                .select { entries.id eq serialNumber }
                .firstOrNull()
                ?.toEntry()
        }
    }

    override fun updateEntry(serialNumber: Long, title: String?, body: String?): Entry? {
        return transaction {
            entries.update({ Entries.id eq serialNumber }) { willUpdate ->
                title?.let {
                    willUpdate[Entries.title] = it
                }
                body?.let {
                    willUpdate[Entries.body] = it
                }
            }
            entries.select { Entries.id eq serialNumber }.firstOrNull()?.toEntry()
        }
    }

    override fun deleteEntry(serialNumber: Long) {
        transaction {
            entries.deleteWhere { entries.id eq serialNumber }
        }
    }

    private fun ResultRow.toEntry(): Entry {
        return Entry(
            get(Entries.id).value,
            get(Entries.title),
            get(Entries.body),
            get(Entries.dateTime).atZone(ZoneId.of("Asia/Tokyo"))
        )
    }
}
