package com.github.hmiyado.kottage.application.plugins.hook

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.ApplicationSendPipeline
import io.ktor.util.AttributeKey
import org.slf4j.Logger

/**
 * 特定のリクエストに反応して外向きのHTTPリクエストを送るプラグイン。
 *
 * フックは [ApplicationSendPipeline.Before]、つまり `call.respond()` が呼ばれた後・
 * レスポンスが実際に送出される前に実行し、**完了を待ってからレスポンスを返す**。
 *
 * 以前は呼び出しパイプラインの `Call` フェーズの直後で実行していたが、その位置では
 * レスポンスの送出が先に完了してしまう。EC2ではプロセスが動き続けるため遅れて送信されて
 * いたが、Lambdaではレスポンスを返した時点でinvocationが終了し実行環境がfreezeされるため、
 * 送信が飛びきる前に凍結してVercelのデプロイフックが発火しなかった。
 *
 * `Call` フェーズの「前」ではハンドラがまだ動いていない（記事が作られていない）ので、
 * 「作成済み」かつ「レスポンス未送出」を満たすのは送信パイプラインの入口だけになる。
 */
class RequestHook(
    configuration: Configuration,
) {
    private val logger = configuration.logger
    private val hooks: List<Hook> = configuration.hooks.toList()

    fun intercept(pipeline: ApplicationCallPipeline) {
        if (hooks.isEmpty()) return

        pipeline.sendPipeline.intercept(ApplicationSendPipeline.Before) { message ->
            val method = call.request.httpMethod
            val path = call.request.path()
            val matched = hooks.filter { hook -> hook.filter(method, path) }
            if (matched.isEmpty()) {
                return@intercept
            }

            // 失敗したリクエストでは発火させない。以前はステータスを見ていなかったため、
            // 例えばCSRFトークン無しの POST /entries が403で弾かれたときにも
            // デプロイフックを叩いてしまい、記事が作られていないのにビルドが走っていた。
            //
            // 判定は「成功なら送る」ではなく「失敗と分かっているときだけ止める」にしている。
            // `call.respond("ok")` のようにステータスを明示せず本文だけを返した場合、
            // 送信パイプラインのどのフェーズでも status() は null のままで、200はエンジンが
            // 最後に補う。「成功と確認できたときだけ送る」にすると、この経路のフックが
            // 黙って落ちる。落とすなら理由が判明しているときだけにする。
            val status = call.response.status() ?: statusOf(message)
            if (status != null && !status.isSuccess()) {
                logger?.debug(
                    "skip {} hook(s) for {} {}: response status is {}",
                    matched.size,
                    method.value,
                    path,
                    status.value,
                )
                return@intercept
            }

            for (hook in matched) {
                try {
                    hook.runner(call)
                } catch (e: Throwable) {
                    // e.message は null になりうるので、それだけを渡すと空行が出るだけで
                    // 何が起きたのか分からなくなる。例外そのものを渡してスタックトレースを残す。
                    logger?.error("request hook failed for ${method.value} $path", e)
                }
            }
        }
    }

    private fun statusOf(message: Any): HttpStatusCode? = message as? HttpStatusCode

    class Configuration {
        var logger: Logger? = null
        val hooks: MutableList<Hook> = mutableListOf()

        fun hook(
            method: HttpMethod,
            path: String,
            runner: suspend ApplicationCall.() -> Unit,
        ) {
            hooks.add(Hook(HookFilter.exactMatch(method, path), runner))
        }

        fun hook(
            filter: HookFilter,
            runner: suspend ApplicationCall.() -> Unit,
        ) {
            hooks.add(Hook(filter, runner))
        }
    }

    companion object Feature : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, RequestHook> {
        override val key: AttributeKey<RequestHook>
            get() = AttributeKey("RequestHook")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit,
        ): RequestHook {
            val configuration = Configuration().apply(configure)
            val feature = RequestHook(configuration)

            feature.intercept(pipeline)

            return feature
        }
    }
}
