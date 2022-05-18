package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.openapi.models.Error403
import com.github.hmiyado.kottage.openapi.models.Error403Cause
import com.github.hmiyado.kottage.openapi.models.Error409

object ErrorFactory {
    fun create403(kind: Error403Cause.Kind? = null) = Error403(
        status = 403,
        description = "Forbidden",
        cause = kind?.let {
            Error403Cause(
                kind = it
            )
        },
    )

    fun create409() = Error409(
        status = 409,
        description = "Conflict"
    )
}
