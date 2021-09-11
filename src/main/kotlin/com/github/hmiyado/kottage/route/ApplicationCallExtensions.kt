package com.github.hmiyado.kottage.route

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import kotlinx.serialization.SerializationException

@Throws(RequestBodyUnrecognizableException::class)
suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(): T {
    return try {
        receive<T>()
    } catch (e: SerializationException) {
        throw RequestBodyUnrecognizableException(e)
    }
}

class RequestBodyUnrecognizableException(override val cause: Throwable?) : SerializationException(cause)
