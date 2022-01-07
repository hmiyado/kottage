package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfHeaderException
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfOriginException
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfTokenException
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

object CsrfStatusPageRouter : StatusPageRouter {
    override fun addStatusPage(configuration: StatusPages.Configuration) {
        with(configuration) {
            exception<CsrfTokenException> {
                call.respond(HttpStatusCode.Forbidden, "Csrf Token is invalid")
            }

            exception<CsrfHeaderException> {
                call.respond(HttpStatusCode.Forbidden, "Csrf Header ${CustomHeaders.XCSRFToken} is required")
            }

            exception<CsrfOriginException> {
                call.respond(HttpStatusCode.Forbidden, "Origin is invalid")
            }
        }
    }
}
