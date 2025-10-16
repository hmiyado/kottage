package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.openapi.models.Error400
import com.github.hmiyado.kottage.openapi.models.Error403
import com.github.hmiyado.kottage.openapi.models.Error403Cause
import com.github.hmiyado.kottage.openapi.models.Error409

object ErrorFactory {
    fun create400(description: String = "BadRequest") =
        Error400(
            status = Error400.Status._400,
            description = Error400.Description.BadRequest,
        )

    fun create403(kind: Error403Cause.Kind? = null) =
        Error403(
            status = Error403.Status._403,
            description = Error403.Description.Forbidden,
            cause =
                kind?.let {
                    Error403Cause(
                        kind = it,
                    )
                },
        )

    fun create409() =
        Error409(
            status = Error409.Status._409,
            description = Error409.Description.Conflict,
        )
}
