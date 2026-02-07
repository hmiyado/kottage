package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.repository.users.UserRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.Users
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.ZoneOffset
import java.time.ZonedDateTime

class EntryCommentRepositoryDatabase : EntryCommentRepository {
    override fun getTotalComments(entrySerialNumber: Long?): Long =
        transaction {
            Comments
                .let {
                    if (entrySerialNumber == null) {
                        it.selectAll()
                    } else {
                        it.selectAll().where { Comments.entry eq entrySerialNumber }
                    }
                }.count()
        }

    override fun getComment(
        entrySerialNumber: Long,
        commentId: Long,
    ): Comment? =
        transaction {
            Comments
                .selectAll()
                .where { (Comments.entry eq entrySerialNumber) and (Comments.id eq commentId) }
                .firstOrNull()
                ?.toComment()
        }

    override fun getComments(
        entrySerialNumber: Long?,
        limit: Long,
        offset: Long,
    ): List<Comment> =
        transaction {
            Comments
                .let {
                    if (entrySerialNumber == null) {
                        it.selectAll()
                    } else {
                        it.selectAll().where { Comments.entry eq entrySerialNumber }
                    }
                }.limit(limit.toInt())
                .offset(offset)
                .orderBy(Comments.createdAt, SortOrder.DESC)
                .map { it.toComment() }
        }

    override fun createComment(
        entrySerialNumber: Long,
        name: String,
        body: String,
        userId: Long?,
    ): Comment =
        transaction {
            val inserted =
                Comments.insert {
                    it[Comments.name] = name
                    it[entry] = entrySerialNumber
                    it[createdAt] = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                    it[Comments.body] = body
                    it[author] = userId?.let { EntityID(userId, Users) }
                }
            inserted.resultedValues?.first()?.toComment() ?: throw IllegalStateException("no comment is created")
        }

    override fun deleteComment(
        entrySerialNumber: Long,
        commentId: Long,
    ) {
        transaction {
            Comments.deleteWhere {
                (entry eq entrySerialNumber) and (id eq commentId)
            }
        }
    }

    private fun ResultRow.toComment() =
        Comment(
            id = get(Comments.id).value,
            entrySerialNumber = get(Comments.entry).value,
            name = get(Comments.name),
            body = get(Comments.body),
            createdAt = get(Comments.createdAt).atZone(ZoneOffset.UTC),
            author =
                get(Comments.author)?.let { userId ->
                    Users
                        .selectAll()
                        .where { Users.id eq userId }
                        .first()
                        .let {
                            with(UserRepositoryDatabase) {
                                it.toUser()
                            }
                        }
                },
        )
}
