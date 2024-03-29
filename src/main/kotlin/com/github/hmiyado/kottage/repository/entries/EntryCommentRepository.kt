package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Comment

interface EntryCommentRepository {

    /**
     * if [entrySerialNumber] is null, return comment count with all entries.
     * if [entrySerialNumber] is not null, return comment count with the specified entry.
     */
    fun getTotalComments(entrySerialNumber: Long?): Long

    fun getComment(entrySerialNumber: Long, commentId: Long): Comment?

    /**
     * if [entrySerialNumber] is null, return comments with all entries.
     * if [entrySerialNumber] is not null, return comments with the specified entry.
     */
    fun getComments(entrySerialNumber: Long?, limit: Long, offset: Long): List<Comment>

    fun createComment(entrySerialNumber: Long, name: String, body: String, userId: Long?): Comment

    fun deleteComment(entrySerialNumber: Long, commentId: Long)
}
