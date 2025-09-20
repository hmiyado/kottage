package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.clientsession.ClientSession
import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.openapi.models.Error403Cause
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.plugins.csrf.CSRF
import io.ktor.server.request.httpMethod
import io.ktor.server.response.respond
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.ktor.ext.get

fun Application.csrf() {
    val randomGenerator = get<RandomGenerator>()

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

        onFailure { originalMessage ->
            // Generate new CSRF token when CSRF check fails
            createNewCsrfToken(randomGenerator)

            val message =
                when {
                    originalMessage.startsWith("missing") -> ErrorFactory.create403(Error403Cause.Kind.CsrfHeaderRequired)
                    originalMessage.startsWith("unexpected") -> ErrorFactory.create403(Error403Cause.Kind.CsrfTokenRequired)
                    else -> {
                        respond(
                            HttpStatusCode.InternalServerError,
                            buildJsonObject {
                                put("status", "500")
                                put("description", "Unexpected status when processing CSRF token")
                            }.toString(),
                        )
                        return@onFailure
                    }
                }
            respond(
                HttpStatusCode.Forbidden,
                message,
            )
        }
    }
    install(createCsrfTokenSessionPlugin(randomGenerator))
}

private fun createCsrfTokenSessionPlugin(randomGenerator: RandomGenerator): ApplicationPlugin<Unit> =
    createApplicationPlugin("CsrfTokenSessionPlugin") {
        onCallRespond { call ->
            if (call.request.httpMethod in listOf(HttpMethod.Head, HttpMethod.Get, HttpMethod.Options)) {
                return@onCallRespond
            }
            val sessions = call.sessions
            val csrfTokenSession = sessions.get<CsrfTokenSession>()
            if (csrfTokenSession == null) {
                call.createNewCsrfToken(randomGenerator)
            }
        }
    }

fun ApplicationCall.createNewCsrfToken(randomGenerator: RandomGenerator) {
    // Use existing ClientSession
    val clientSession = sessions.get<ClientSession>()
    if (clientSession != null) {
        val token = randomGenerator.generateString()
        sessions.set(CsrfTokenSession(token, clientSession))
    }
}
