package com.github.hmiyado.application

import com.github.hmiyado.application.configuration.provideApplicationConfigurationModule
import com.github.hmiyado.authentication.admin
import com.github.hmiyado.authentication.authenticationModule
import com.github.hmiyado.repository.initializeDatabase
import com.github.hmiyado.repository.repositoryModule
import com.github.hmiyado.route.routing
import com.github.hmiyado.service.serviceModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.serialization.json
import org.koin.core.logger.PrintLogger
import org.koin.core.qualifier.named
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
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        // this must be first because this becomes default ContentType
        json(contentType = ContentType.Application.Json)
        json(contentType = ContentType.Any)
        json(contentType = ContentType.Text.Any)
        json(contentType = ContentType.Text.Plain)
    }
    install(Authentication) {
        admin(get(qualifier = named("admin")))
    }
    routing()
}
