package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.route.entries.EntriesLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsCommentIdLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberLocation
import com.github.hmiyado.kottage.route.health.HealthLocation
import com.github.hmiyado.kottage.route.users.UsersAdminsLocation
import com.github.hmiyado.kottage.route.users.UsersIdLocation
import com.github.hmiyado.kottage.route.users.UsersLocation
import io.ktor.application.Application
import io.ktor.routing.routing
import org.koin.ktor.ext.get

fun Application.routing() {
    routing {
        RootLocation().addRoute(this)
        EntriesLocation(get()).addRoute(this)
        EntriesSerialNumberLocation(get()).addRoute(this)
        EntriesSerialNumberCommentsLocation(get()).addRoute(this)
        EntriesSerialNumberCommentsCommentIdLocation(get()).addRoute(this)
        UsersLocation.addRoute(this, get())
        UsersIdLocation.addRoute(this, get())
        UsersAdminsLocation(get(), get()).addRoute(this)
        HealthLocation.addRoute(this, get())
    }
}
