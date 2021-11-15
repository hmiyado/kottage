package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.configuration.provideApplicationConfigurationModule
import com.github.hmiyado.kottage.authentication.admin
import com.github.hmiyado.kottage.authentication.authenticationModule
import com.github.hmiyado.kottage.authentication.sessionExpiration
import com.github.hmiyado.kottage.authentication.users
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.repository.initializeDatabase
import com.github.hmiyado.kottage.repository.repositoryModule
import com.github.hmiyado.kottage.route.routing
import com.github.hmiyado.kottage.service.serviceModule
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
import org.koin.core.logger.PrintLogger
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
fun Application.main() {
    install(Koin) {
        logger(PrintLogger())
        modules(
            provideApplicationConfigurationModule(environment.config),
            repositoryModule,
            serviceModule,
            authenticationModule
        )
    }
    initializeDatabase(get())
    install(CallLogging)
    defaultHeaders()
    install(CORS) {
        allowCredentials = true
        host("localhost:3000")
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
                DevelopmentConfiguration.Production -> true
            }
            cookie.maxAgeInSeconds = sessionExpiration.seconds
        }
    }
    install(Locations)
    routing()
}
