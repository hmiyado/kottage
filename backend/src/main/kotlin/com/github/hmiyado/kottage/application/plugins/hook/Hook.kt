package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.route.matchesConcretePath
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall

data class Hook(
    val filter: HookFilter,
    val runner: suspend ApplicationCall.() -> Unit,
)

/**
 * どのリクエストにフックを反応させるかを決める述語。
 *
 * 実行位置は [RequestHook] が送信パイプラインの入口に固定しているため、
 * ここでパイプラインフェーズを選ぶことはできない。以前は `pipelinePhase` と
 * `insertAfter` を持っていたが、どちらも既定値のままでしか使われておらず、
 * かつ既定の「`Call` フェーズの直後」がLambdaでフックを取りこぼす原因だった。
 */
abstract class HookFilter : (HttpMethod, String) -> Boolean {
    companion object {
        fun exactMatch(
            method: HttpMethod,
            path: String,
        ) = object : HookFilter() {
            override fun invoke(
                p1: HttpMethod,
                p2: String,
            ): Boolean = p1 == method && path.matchesConcretePath(p2)
        }

        fun match(block: (HttpMethod, String) -> Boolean = { _, _ -> false }) =
            object : HookFilter() {
                override fun invoke(
                    p1: HttpMethod,
                    p2: String,
                ): Boolean = block(p1, p2)
            }
    }
}
