package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.openapi.Paths
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.HttpMethod

val csrfTargetOperation = listOf(
    HttpMethod.Delete to Paths.usersIdDelete
)

fun Application.csrf() {
    install(Csrf) {
        requestFilter { httpMethod, path ->
            csrfTargetOperation
                .any { (operationMethod, operationPath) ->
                    operationMethod == httpMethod && operationPath == path
                }
        }
        session<ClientSession> {
            onFail {
                throw CsrfTokenException()
            }
        }
    }
}

class CsrfTokenException() : IllegalStateException()
