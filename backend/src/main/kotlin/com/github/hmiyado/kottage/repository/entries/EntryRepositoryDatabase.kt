package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Entry
import com.github.hmiyado.kottage.repository.users.UserRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.ZoneOffset
import java.time.ZonedDateTime

class EntryRepositoryDatabase : EntryRepository {
    override fun getEntryTotalCount(): Long =
        transaction {
            Entries.selectAll().count()
        }

    override fun getEntries(
        limit: Long,
        offset: Long,
    ): List<Entry> =
        transaction {
            val rows =
                Entries
                    .selectAll()
                    .orderBy(Entries.id, SortOrder.DESC)
                    .limit(limit.toInt())
                    .offset(offset)
                    .toList()

            val entryIds = rows.map { it[Entries.id].value }
            val authorIds = rows.map { it[Entries.author].value }.distinct()

            val commentCountsByEntry: Map<Long, Long> =
                if (entryIds.isEmpty()) {
                    emptyMap()
                } else {
                    Comments
                        .selectAll()
                        .where { Comments.entry inList entryIds }
                        .groupBy { it[Comments.entry].value }
                        .mapValues { (_, comments) -> comments.size.toLong() }
                }

            val authorsById =
                if (authorIds.isEmpty()) {
                    emptyMap()
                } else {
                    Users
                        .selectAll()
                        .where { Users.id inList authorIds }
                        .associateBy({ it[Users.id].value }) {
                            with(UserRepositoryDatabase) { it.toUser() }
                        }
                }

            rows.map { row ->
                val entryId = row[Entries.id].value
                Entry(
                    serialNumber = entryId,
                    title = row[Entries.title],
                    body = row[Entries.body],
                    dateTime = row[Entries.dateTime].atZone(ZoneOffset.UTC),
                    commentsTotalCount = commentCountsByEntry[entryId] ?: 0,
                    author = authorsById.getValue(row[Entries.author].value),
                )
            }
        }

    override fun createEntry(
        title: String,
        body: String,
        userId: Long,
    ): Entry =
        transaction {
            val id =
                Entries.insertAndGetId {
                    it[Entries.title] = title
                    it[Entries.body] = body
                    it[dateTime] = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                    it[author] = userId
                }
            Entries
                .selectAll()
                .where { Entries.id eq id }
                .first()
                .toEntry()
        }

    override fun getEntry(serialNumber: Long): Entry? =
        transaction {
            Entries
                .selectAll()
                .where { Entries.id eq serialNumber }
                .firstOrNull()
                ?.toEntry()
        }

    override fun updateEntry(
        serialNumber: Long,
        userId: Long,
        title: String?,
        body: String?,
    ): Entry? =
        transaction {
            Entries.update({ Entries.id eq serialNumber and (Entries.author eq userId) }) { willUpdate ->
                title?.let {
                    willUpdate[Entries.title] = it
                }
                body?.let {
                    willUpdate[Entries.body] = it
                }
            }
            Entries
                .selectAll()
                .where { Entries.id eq serialNumber }
                .firstOrNull()
                ?.toEntry()
        }

    override fun deleteEntry(serialNumber: Long) {
        transaction {
            Entries.deleteWhere { id eq serialNumber }
        }
    }

    private fun ResultRow.toEntry(): Entry =
        Entry(
            serialNumber = get(Entries.id).value,
            title = get(Entries.title),
            body = get(Entries.body),
            dateTime = get(Entries.dateTime).atZone(ZoneOffset.UTC),
            commentsTotalCount =
                Comments
                    .selectAll()
                    .where { Comments.entry eq get(Entries.id).value }
                    .count(),
            author =
                Users
                    .selectAll()
                    .where { Users.id eq get(Entries.author) }
                    .first()
                    .let {
                        with(UserRepositoryDatabase) {
                            it.toUser()
                        }
                    },
        )
}
