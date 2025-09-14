package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.openapi.models.Error403Cause
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.csrf.CSRF
import io.ktor.server.response.respond
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

fun Application.csrf() {
    install(CSRF) {
        // CORS でも Origin のチェックをしているので、CSRF ではチェックしない
        // allowOrigin(origin)

        // custom header checks
        checkHeader(CustomHeaders.XCSRFToken) { headerValue ->
            // ClientSession をとってくる
            // ClientSession に紐づく CSRF トークンを取得する
            // 一致するかどうかを比較する
            val clientSession = sessions.get<ClientSession>() ?: return@checkHeader false
            val csrfTokenSession = sessions.get<CsrfTokenSession>() ?: return@checkHeader false
            return@checkHeader csrfTokenSession.clientSession == clientSession
        }

        onFailure { message ->
            respond(
                HttpStatusCode.Forbidden,
                when {
                    message.startsWith("missing") -> ErrorFactory.create403(Error403Cause.Kind.CsrfHeaderRequired)
                    message.startsWith("unexpected") -> ErrorFactory.create403(Error403Cause.Kind.CsrfTokenRequired)
                    else -> ErrorFactory.create403()
                },
            )

        }
    }
}

class CsrfOriginException : IllegalStateException()

class CsrfHeaderException : IllegalStateException()
