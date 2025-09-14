package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsCommentIdLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberLocation
import org.koin.core.qualifier.named
import org.koin.dsl.module

val statusPagesModule = module {
    single(named("StatusPageRouter")) {
        listOf(
            OpenApiStatusPageRouter,
            EntriesSerialNumberLocation.Companion,
            EntriesSerialNumberCommentsCommentIdLocation.Companion,
        )
    }
}
