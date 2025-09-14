package com.github.hmiyado.kottage.application.plugins.clientsession

import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.response.header
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun createClientSessionPlugin(randomGenerator: RandomGenerator): ApplicationPlugin<Unit> =
    createApplicationPlugin("ClientSessionPlugin") {
        onCall { call ->
            // Insert ClientSession for all requests
            val sessions = call.sessions
            val clientSession = sessions.get<ClientSession>()
            if (clientSession == null) {
                call.createNewClientSession(randomGenerator)
            }
        }
    }

fun ApplicationCall.createNewClientSession(randomGenerator: RandomGenerator) {
    val sessionToken = randomGenerator.generateString()
    sessions.set(ClientSession(sessionToken))
    response.cookies.append(Cookie("client_session", sessionToken, secure = true))
}
