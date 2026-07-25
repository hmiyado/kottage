package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.requestHook() {
    val logger = LoggerFactory.getLogger("Application")
    install(RequestHook) {
        this.logger = logger
        outgoingWebhook(logger, get(), get(named("HookConfigurations")))
    }
}

private fun RequestHook.Configuration.outgoingWebhook(
    logger: Logger,
    client: HttpClient,
    hookConfigurations: List<HookConfiguration>,
) {
    for (configuration in hookConfigurations) {
        hook(configuration.method, configuration.path) {
            val response = client.post(configuration.requestTo) {}
            // KtorのHttpClientは既定で expectSuccess = false なので、4xx/5xx でも例外に
            // ならず素通りする。呼び出し先が「受け付けた」のか「拒否した」のかは、ここで
            // ステータスを見ない限り一切分からない。
            //
            // 実際にこれで嵌った: Lambda移行後、フックは送信されているのにVercelの
            // デプロイが作られない状態があり、成功と失敗を区別できないまま原因の
            // 切り分けに時間を要した。
            if (response.status.isSuccess()) {
                logger.info(
                    "request hook to {} succeeded with {}",
                    configuration.requestTo,
                    response.status.value,
                )
            } else {
                logger.error(
                    "request hook to {} failed with {}: {}",
                    configuration.requestTo,
                    response.status.value,
                    response.bodyAsText().take(RESPONSE_BODY_LOG_LIMIT),
                )
            }
        }
    }
}

/** 呼び出し先が長い本文を返してもログを溢れさせないための上限。 */
private const val RESPONSE_BODY_LOG_LIMIT = 500
