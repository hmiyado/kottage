package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.route.entries.entries
import com.github.hmiyado.kottage.route.entries.entriesSerialNumber
import com.github.hmiyado.kottage.route.health.HealthLocation
import com.github.hmiyado.kottage.route.users.UsersIdLocation
import com.github.hmiyado.kottage.route.users.UsersLocation
import io.ktor.application.Application
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.routing.routing
import org.koin.ktor.ext.get

@KtorExperimentalLocationsAPI
fun Application.routing() {
    routing {
        helloWorld()
        entries(get())
        entriesSerialNumber(get())
        UsersLocation.addRoute(this, get())
        UsersIdLocation.addRoute(this, get())
        HealthLocation.addRoute(this, get())
    }
}
