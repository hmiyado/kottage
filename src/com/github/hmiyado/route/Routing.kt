package com.github.hmiyado.route

import com.github.hmiyado.route.entries.entriesSerialNumber
import com.github.hmiyado.route.users.UsersIdLocation
import com.github.hmiyado.route.users.UsersLocation
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
    }
}
