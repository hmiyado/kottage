package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.repository.users.UserRepositoryDatabase
import com.github.hmiyado.kottage.repository.users.Users
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class EntryCommentRepositoryDatabase : EntryCommentRepository {
    override fun getTotalComments(entrySerialNumber: Long): Long {
        return transaction {
            Comments.select { Comments.entry eq entrySerialNumber }.count()
        }
    }

    override fun getComment(entrySerialNumber: Long, commentId: Long): Comment? {
        return transaction {
            Comments
                .select { (Comments.entry eq entrySerialNumber) and (Comments.idByEntry eq commentId) }
                .firstOrNull()
                ?.toComment()
        }
    }

    override fun getComments(entrySerialNumber: Long, limit: Long, offset: Long): List<Comment> {
        return transaction {
            Comments
                .select { Comments.entry eq entrySerialNumber }
                .limit(limit.toInt(), offset = offset)
                .orderBy(Comments.idByEntry, SortOrder.DESC)
                .map { it.toComment() }
        }
    }

    override fun createComment(entrySerialNumber: Long, name: String, body: String, userId: Long?): Comment {
        return transaction {
            val lastCommentId = Comments.select { Comments.entry eq entrySerialNumber }.count()
            val inserted = Comments.insert {
                it[idByEntry] = lastCommentId + 1
                it[entry] = entrySerialNumber
                it[createdAt] = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                it[Comments.body] = body
                it[author] = userId
            }
            inserted.resultedValues?.first()?.toComment() ?: throw IllegalStateException("no comment is created")
        }
    }

    override fun deleteComment(entrySerialNumber: Long, commentId: Long) {
        transaction {
            Comments.deleteWhere {
                (Comments.entry eq entrySerialNumber) and (Comments.idByEntry eq commentId)
            }
        }
    }

    private fun ResultRow.toComment() = Comment(
        id = get(Comments.idByEntry),
        body = get(Comments.body),
        createdAt = get(Comments.createdAt).atZone(ZoneOffset.UTC),
        author = Users
            .select { Users.id eq get(Comments.author) }
            .first()
            .let {
                with(UserRepositoryDatabase) {
                    it.toUser()
                }
            }
    )
}
