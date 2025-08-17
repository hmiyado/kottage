package com.github.hmiyado.kottage.model

import com.github.hmiyado.kottage.model.validator.InvalidParametersException
import com.github.hmiyado.kottage.model.validator.StringValidator
import java.time.ZonedDateTime

data class Comment(
    val id: Long = 0,
    val entrySerialNumber: Long = 0,
    val name: String = "name",
    val body: String = "_",
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val author: User? = null,
) {
    init {
        listOfNotNull(
            if (!StringValidator.validateName(name)) {
                "name"
            } else {
                null
            },
            if (body.isEmpty() || body.isBlank()) {
                "body"
            } else {
                null
            }
        ).let {
            if (it.isNotEmpty()) {
                throw InvalidParametersException(it)
            }
        }
    }
}
