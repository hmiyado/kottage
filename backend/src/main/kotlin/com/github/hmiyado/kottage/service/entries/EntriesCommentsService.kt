package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.entries.EntryCommentRepository
import com.github.hmiyado.kottage.repository.entries.EntryRepository

interface EntriesCommentsService {
    @Throws(EntriesService.NoSuchEntryException::class)
    fun getComments(
        entrySerialNumber: Long?,
        limit: Long?,
        offset: Long?,
    ): Page<Comment>

    @Throws(EntriesService.NoSuchEntryException::class)
    fun addComment(
        entrySerialNumber: Long,
        name: String,
        body: String,
        author: User?,
    ): Comment

    @Throws(
        EntriesService.NoSuchEntryException::class,
        NoSuchCommentException::class,
        ForbiddenOperationException::class,
    )
    fun removeComment(
        entrySerialNumber: Long,
        commentId: Long,
        user: User,
    )

    companion object {
        const val defaultLimit = 20L
        const val maxLimit = 100L
        const val defaultOffset = 0L
    }

    data class NoSuchCommentException(
        val commentId: Long,
    ) : NoSuchElementException("No comment with id: $commentId")

    data class ForbiddenOperationException(
        val serialNumber: Long,
        val commentId: Long,
        val userId: Long,
    ) : IllegalStateException("user $userId cannot operate comment $commentId of entry $serialNumber")
}

class EntriesCommentsServiceImpl(
    private val entryRepository: EntryRepository,
    private val entryCommentRepository: EntryCommentRepository,
) : EntriesCommentsService {
    override fun getComments(
        entrySerialNumber: Long?,
        limit: Long?,
        offset: Long?,
    ): Page<Comment> {
        val actualLimit = minOf(limit ?: EntriesCommentsService.defaultLimit, EntriesCommentsService.maxLimit)
        val actualOffset = offset ?: EntriesCommentsService.defaultOffset

        if (entrySerialNumber != null) {
            entryRepository.getEntry(entrySerialNumber) ?: throw EntriesService.NoSuchEntryException(entrySerialNumber)
        }

        val comments = entryCommentRepository.getComments(entrySerialNumber, actualLimit, actualOffset)
        return Page(
            totalCount = entryCommentRepository.getTotalComments(entrySerialNumber),
            items = comments,
        )
    }

    override fun addComment(
        entrySerialNumber: Long,
        name: String,
        body: String,
        author: User?,
    ): Comment {
        entryRepository.getEntry(entrySerialNumber) ?: throw EntriesService.NoSuchEntryException(entrySerialNumber)
        return entryCommentRepository.createComment(entrySerialNumber, name, body, author?.id)
    }

    override fun removeComment(
        entrySerialNumber: Long,
        commentId: Long,
        user: User,
    ) {
        entryRepository.getEntry(entrySerialNumber) ?: throw EntriesService.NoSuchEntryException(entrySerialNumber)
        val comment =
            entryCommentRepository.getComment(entrySerialNumber, commentId)
                ?: throw EntriesCommentsService.NoSuchCommentException(commentId)
        if (comment.author != user) {
            throw EntriesCommentsService.ForbiddenOperationException(entrySerialNumber, commentId, user.id)
        }
        entryCommentRepository.deleteComment(entrySerialNumber, comment.id)
    }
}
