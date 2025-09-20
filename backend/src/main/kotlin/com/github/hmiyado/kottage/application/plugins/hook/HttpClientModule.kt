package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.model.Health
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.userAgent
import org.koin.dsl.module

val httpClientModule =
    module {
        single {
            HttpClient(CIO) {
                defaultRequest {
                    val version: Health.Version = get()
                    userAgent("Kottage/$version ( https://github.com/hmiyado/kottage )")
                }
            }
        }
    }
