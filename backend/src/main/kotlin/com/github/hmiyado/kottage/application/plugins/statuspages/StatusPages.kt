package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

fun Application.statusPages() {
    install(StatusPages) {
        for (router in this@statusPages.get<List<StatusPageRouter>>(named("StatusPageRouter"))) {
            router.addStatusPage(this)
        }
    }
}
