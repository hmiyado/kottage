package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.authentication.admin
import com.github.hmiyado.kottage.authentication.sessionExpiration
import com.github.hmiyado.kottage.authentication.users
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.repository.initializeDatabase
import com.github.hmiyado.kottage.route.routing
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get

@KtorExperimentalLocationsAPI
fun Application.main() {
    install(Koin) {
        initializeKoinModules(this@main.environment)
    }
    initializeDatabase(get())
    install(CallLogging)
    defaultHeaders()
    install(CORS) {
        allowCredentials = true
        when (get<DevelopmentConfiguration>()) {
            DevelopmentConfiguration.Development -> host("localhost:3000")
            DevelopmentConfiguration.Production -> {
                host("miyado.dev", schemes = listOf("https"), subDomains = listOf("www"))
            }
        }
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Patch)
        method(HttpMethod.Delete)
        header(HttpHeaders.ContentType)
    }
    install(AutoHeadResponse)
    statusPages()
    contentNegotiation()
    install(Authentication) {
        admin(get(), get(), get())
        users(get())
    }
    install(Sessions) {
        cookie<UserSession>("user_session", storage = get()) {
            cookie.httpOnly = false
            cookie.extensions["SameSite"] = "Strict"
            cookie.secure = when (get<DevelopmentConfiguration>()) {
                DevelopmentConfiguration.Development -> false
                // todo: secure should be true in production, but infra architecture is not match now (lb -> app is http)
                // cors requires https, so that non-secure browser may not be able to get cookie
                DevelopmentConfiguration.Production -> false
            }
            cookie.maxAgeInSeconds = sessionExpiration.seconds
        }
    }
    install(Locations)
    routing()
}
