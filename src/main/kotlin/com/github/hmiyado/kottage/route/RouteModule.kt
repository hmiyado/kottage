package com.github.hmiyado.kottage.route

import com.github.hmiyado.kottage.route.entries.EntriesLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsCommentIdLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberCommentsLocation
import com.github.hmiyado.kottage.route.entries.EntriesSerialNumberLocation
import com.github.hmiyado.kottage.route.health.HealthLocation
import com.github.hmiyado.kottage.route.oauth.OauthGoogleLocation
import com.github.hmiyado.kottage.route.users.UsersAdminsLocation
import com.github.hmiyado.kottage.route.users.UsersIdLocation
import com.github.hmiyado.kottage.route.users.UsersLocation
import org.koin.core.qualifier.named
import org.koin.dsl.module

val routeModule = module {
    single {
        listOf(
            RootLocation(),
            EntriesLocation(get()),
            EntriesSerialNumberLocation(get()),
            EntriesSerialNumberCommentsLocation(get(), get()),
            EntriesSerialNumberCommentsCommentIdLocation(get()),
            UsersLocation(get()),
            UsersIdLocation(get()),
            UsersAdminsLocation(get(), get()),
            HealthLocation(get()),
            OauthGoogleLocation(get(), get(named("oauth-redirects"))),
        )
    }
}
