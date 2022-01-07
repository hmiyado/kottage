package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.StatusPages
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

fun Application.statusPages() {
    install(StatusPages) {
        for (router in get<List<StatusPageRouter>>(named("StatusPageRouter"))) {
            router.addStatusPage(this)
        }
    }
}
