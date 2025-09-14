package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.User

class EntryCommentRepositoryMemory : EntryCommentRepository {
    private val comments = (1..10).map { Comment(id = it.toLong()) }

    override fun getTotalComments(entrySerialNumber: Long?): Long = comments.size.toLong()

    override fun getComment(
        entrySerialNumber: Long,
        commentId: Long,
    ): Comment? = comments.firstOrNull()

    override fun getComments(
        entrySerialNumber: Long?,
        limit: Long,
        offset: Long,
    ): List<Comment> = comments

    override fun createComment(
        entrySerialNumber: Long,
        name: String,
        body: String,
        userId: Long?,
    ): Comment =
        Comment(
            body = body,
            author =
                userId?.let {
                    User(id = userId)
                },
        )

    override fun deleteComment(
        entrySerialNumber: Long,
        commentId: Long,
    ) {
    }
}
