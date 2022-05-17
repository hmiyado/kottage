package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.application.plugins.csrf.CsrfHeaderException
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfOriginException
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfTokenException
import com.github.hmiyado.kottage.openapi.models.Error403Cause
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

object CsrfStatusPageRouter : StatusPageRouter {
    override fun addStatusPage(configuration: StatusPagesConfig) {
        with(configuration) {
            exception<CsrfTokenException> { call, _ ->
                call.respond(HttpStatusCode.Forbidden, ErrorFactory.create403(
                    kind = Error403Cause.Kind.CsrfTokenRequired
                ))
            }

            exception<CsrfHeaderException> { call, _ ->
                call.respond(HttpStatusCode.Forbidden, ErrorFactory.create403(
                    kind = Error403Cause.Kind.CsrfHeaderRequired
                ))
            }

            exception<CsrfOriginException> {call, _ ->
                call.respond(HttpStatusCode.Forbidden, ErrorFactory.create403(
                    kind = Error403Cause.Kind.InvalidOrigin
                ))
            }
        }
    }
}
