package com.github.hmiyado.kottage.application.plugins

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.authentication.sessionExpiration
import com.github.hmiyado.kottage.application.plugins.csrf.ClientSession
import com.github.hmiyado.kottage.application.plugins.csrf.CsrfTokenSession
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.CurrentSession
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.header
import io.ktor.server.sessions.set
import org.koin.ktor.ext.get

fun Application.sessions() {
    install(Sessions) {
        cookie<UserSession>("user_session", storage = this@sessions.get()) {
            cookie.httpOnly = false
            cookie.extensions["SameSite"] = "Strict"
            cookie.secure = when (this@sessions.get<DevelopmentConfiguration>()) {
                DevelopmentConfiguration.Development -> false
                // todo: secure should be true in production, but infra architecture is not match now (lb -> app is http)
                // cors requires https, so that non-secure browser may not be able to get cookie
                DevelopmentConfiguration.Production -> false
            }
            cookie.maxAgeInSeconds = sessionExpiration.seconds
        }
        cookie<ClientSession>("client_session", storage = this@sessions.get()) {
            cookie.httpOnly = false
            cookie.extensions["SameSite"] = "Strict"
            cookie.secure = when (this@sessions.get<DevelopmentConfiguration>()) {
                DevelopmentConfiguration.Development -> false
                // todo: secure should be true in production, but infra architecture is not match now (lb -> app is http)
                // cors requires https, so that non-secure browser may not be able to get cookie
                DevelopmentConfiguration.Production -> false
            }
            cookie.maxAgeInSeconds = sessionExpiration.seconds
        }
        header<CsrfTokenSession>(CustomHeaders.XCSRFToken, storage = this@sessions.get())
    }
}
