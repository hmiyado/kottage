package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.repository.users.UserRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.Users
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class EntryRepositoryDatabase : EntryRepository {
    override fun getEntryTotalCount(): Long {
        return transaction {
            Entries.selectAll().count()
        }
    }

    override fun getEntries(limit: Long, offset: Long): List<Entry> {
        return transaction {
            Entries
                .selectAll()
                .orderBy(Entries.id, SortOrder.DESC)
                .limit(limit.toInt(), offset = offset)
                .map { it.toEntry() }
        }
    }

    override fun createEntry(title: String, body: String, userId: Long): Entry {
        return transaction {
            val id = Entries.insertAndGetId {
                it[Entries.title] = title
                it[Entries.body] = body
                it[dateTime] = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
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

    override fun updateEntry(serialNumber: Long, userId: Long, title: String?, body: String?): Entry? {
        return transaction {
            Entries.update({ Entries.id eq serialNumber and (Entries.author eq userId) }) { willUpdate ->
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
            Entries.deleteWhere { id eq serialNumber }
        }
    }

    private fun ResultRow.toEntry(): Entry {
        return Entry(
            serialNumber = get(Entries.id).value,
            title = get(Entries.title),
            body = get(Entries.body),
            dateTime = get(Entries.dateTime).atZone(ZoneOffset.UTC),
            commentsTotalCount = Comments
                .select { Comments.entry eq get(Entries.id).value }
                .count(),
            author = Users
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
