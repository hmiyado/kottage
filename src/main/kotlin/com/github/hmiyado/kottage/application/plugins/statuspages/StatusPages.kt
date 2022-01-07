package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsCommentIdLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberLocation
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.StatusPages

fun Application.statusPages() {
    install(StatusPages) {
        OpenApiStatusPageRouter.addStatusPage(this)

        CsrfStatusPageRouter.addStatusPage(this)

        EntriesSerialNumberLocation.addStatusPage(this)

        EntriesSerialNumberCommentsCommentIdLocation.addStatusPage(this)
    }
}
