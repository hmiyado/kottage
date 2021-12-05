package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.entries.EntryCommentRepository

interface EntriesCommentsService {
    fun getComments(entrySerialNumber: Long, limit: Long?, offset: Long?): Page<Comment>

    fun addComment(entrySerialNumber: Long, body: String, author: User): Comment

    companion object {
        const val defaultLimit = 20L
        const val maxLimit = 100L
        const val defaultOffset = 0L
    }
}

class EntriesCommentsServiceImpl(
    private val entryCommentRepository: EntryCommentRepository
) : EntriesCommentsService {
    override fun getComments(entrySerialNumber: Long, limit: Long?, offset: Long?): Page<Comment> {
        val actualLimit = minOf(limit ?: EntriesCommentsService.defaultLimit, EntriesCommentsService.maxLimit)
        val actualOffset = offset ?: EntriesCommentsService.defaultOffset
        val comments = entryCommentRepository.getComments(entrySerialNumber, actualLimit, actualOffset)
        return Page(
            totalCount = entryCommentRepository.getTotalComments(entrySerialNumber),
            items = comments
        )
    }

    override fun addComment(entrySerialNumber: Long, body: String, author: User): Comment {
        return entryCommentRepository.createComment(entrySerialNumber, body, author.id)
    }

}
