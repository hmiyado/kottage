package com.github.hmiyado.kottage.application.plugins.statuspages

import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsCommentIdLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberLocation
import com.github.hmiyado.kottage.route.oauth.OauthGoogleStatusPageRouter
import org.koin.core.qualifier.named
import org.koin.dsl.module

val statusPagesModule = module {
    single(named("StatusPageRouter")) {
        listOf(
            OpenApiStatusPageRouter,
            CsrfStatusPageRouter,
            EntriesSerialNumberLocation.Companion,
            EntriesSerialNumberCommentsCommentIdLocation.Companion,
            OauthGoogleStatusPageRouter,
        )
    }
}
