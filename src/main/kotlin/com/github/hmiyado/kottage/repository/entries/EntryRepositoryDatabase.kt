package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.repository.users.UserRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.Users
import java.time.LocalDateTime
import java.time.ZoneId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class EntryRepositoryDatabase : EntryRepository {
    override fun getEntries(): List<Entry> {
        return transaction {
            Entries
                .selectAll()
                .map { it.toEntry() }
        }
    }

    override fun createEntry(title: String, body: String, userId: Long): Entry {
        return transaction {
            val id = Entries.insertAndGetId {
                it[Entries.title] = title
                it[Entries.body] = body
                it[dateTime] = LocalDateTime.now()
                it[author] = userId
            }
            Entries
                .select { Entries.id eq id }
                .first()
                .toEntry()
        }
    }

    override fun getEntry(serialNumber: Long): Entry? {
        return transaction {
            Entries
                .select { Entries.id eq serialNumber }
                .firstOrNull()
                ?.toEntry()
        }
    }

    override fun updateEntry(serialNumber: Long, title: String?, body: String?): Entry? {
        return transaction {
            Entries.update({ Entries.id eq serialNumber }) { willUpdate ->
                title?.let {
                    willUpdate[Entries.title] = it
                }
                body?.let {
                    willUpdate[Entries.body] = it
                }
            }
            Entries.select { Entries.id eq serialNumber }.firstOrNull()?.toEntry()
        }
    }

    override fun deleteEntry(serialNumber: Long) {
        transaction {
            Entries.deleteWhere { Entries.id eq serialNumber }
        }
    }

    private fun ResultRow.toEntry(): Entry {
        return Entry(
            get(Entries.id).value,
            get(Entries.title),
            get(Entries.body),
            get(Entries.dateTime).atZone(ZoneId.of("Asia/Tokyo")),
            Users
                .select { Users.id eq get(Entries.author) }
                .first()
                .let {
                    with(UserRepositoryDatabase) {
                        it.toUser()
                    }
                },
        )
    }
}
