package com.github.hmiyado.kottage.repository.entries

import com.github.hmiyado.kottage.model.Comment

interface EntryCommentRepository {

    fun getTotalComments(entrySerialNumber: Long): Long

    fun getComments(entrySerialNumber: Long, limit: Long, offset: Long): List<Comment>

    fun createComment(entrySerialNumber: Long, body: String, userId: Long): Comment
}
