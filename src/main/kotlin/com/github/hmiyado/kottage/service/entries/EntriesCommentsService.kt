package com.github.hmiyado.kottage.service.entries

import com.github.hmiyado.kottage.model.Comment
import com.github.hmiyado.kottage.model.Page
import com.github.hmiyado.kottage.model.User

interface EntriesCommentsService {
    fun getComments(entrySerialNumber: Long, limit: Long?, offset: Long?): Page<Comment>

    fun addComment(entrySerialNumber: Long, body: String, author: User): Comment
}
