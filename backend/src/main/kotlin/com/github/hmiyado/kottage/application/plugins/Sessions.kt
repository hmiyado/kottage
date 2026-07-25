package com.github.hmiyado.kottage.application.plugins

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.configuration.SessionSignKey
import com.github.hmiyado.kottage.application.plugins.authentication.sessionExpiration
import com.github.hmiyado.kottage.application.plugins.clientsession.ClientSession
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfTokenSession
import com.github.hmiyado.kottage.model.UserSession
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.header
import org.koin.ktor.ext.get

fun Application.sessions() {
    // No SessionStorage is registered: the entire (signed) session value round-trips through the
    // client via cookie/header, so no process holds state that a Lambda recycle could lose.
    val signKey = get<SessionSignKey>().bytes
    val isProduction = get<DevelopmentConfiguration>() == DevelopmentConfiguration.Production
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.httpOnly = true
            cookie.extensions["SameSite"] = "Strict"
            // Secure only gates the browser -> API Gateway hop, which is HTTPS via ACM. The
            // internal API Gateway -> app hop is invisible to the browser, so this is safe.
            cookie.secure = isProduction
            cookie.maxAgeInSeconds = sessionExpiration.seconds
            transform(SessionTransportTransformerMessageAuthentication(signKey))
        }
        cookie<ClientSession>("client_session") {
            cookie.httpOnly = true
            cookie.extensions["SameSite"] = "Strict"
            cookie.secure = isProduction
            cookie.maxAgeInSeconds = sessionExpiration.seconds
            transform(SessionTransportTransformerMessageAuthentication(signKey))
        }
        header<CsrfTokenSession>(CustomHeaders.X_CSRF_TOKEN) {
            transform(SessionTransportTransformerMessageAuthentication(signKey))
        }
    }
}
