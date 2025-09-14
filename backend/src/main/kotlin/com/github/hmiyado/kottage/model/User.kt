package com.github.hmiyado.kottage.model

import com.github.hmiyado.kottage.model.validator.InvalidParametersException
import com.github.hmiyado.kottage.model.validator.StringValidator
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val screenName: String = "default",
) {
    init {
        listOfNotNull(
            if (!StringValidator.validateName(screenName)) {
                "screenName"
            } else {
                null
            },
        ).let {
            if (it.isNotEmpty()) {
                throw InvalidParametersException(it)
            }
        }
    }
}
