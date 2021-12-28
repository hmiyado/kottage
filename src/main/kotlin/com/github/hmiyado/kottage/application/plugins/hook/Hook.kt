package com.github.hmiyado.kottage.application.plugins.hook

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod

data class Hook(
    val filter: HookFilter,
    val runner: suspend ApplicationCall.() -> Unit,
)

abstract class HookFilter() : (HttpMethod, String) -> Boolean {
    companion object {
        fun exactMatch(method: HttpMethod, path: String) = object : HookFilter() {
            override fun invoke(p1: HttpMethod, p2: String): Boolean {
                return p1 == method && p2 == path
            }
        }

        fun match(block: (HttpMethod, String) -> Boolean) = object : HookFilter() {
            override fun invoke(p1: HttpMethod, p2: String): Boolean {
                return block(p1, p2)
            }
        }
    }
}
