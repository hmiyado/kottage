package com.github.hmiyado.kottage.authentication

import io.ktor.sessions.SessionStorage
import io.ktor.util.toByteArray
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writer
import java.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import redis.clients.jedis.JedisPool

class SessionStorageRedis(private val jedisPool: JedisPool, private val keyPrefix: String, val expires: Duration) :
    SessionStorage {
    private fun String.withKeyPrefix() = "$keyPrefix:$this"

    override suspend fun invalidate(id: String) {
        jedisPool.resource.use {
            it.del(id.withKeyPrefix())
        }
    }

    override suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R {
        val key = id.withKeyPrefix()
        return jedisPool.resource.use {
            val session = it[key]?.toByteArray() ?: throw NoSuchElementException("Session $id not found")
            it.expire(key, expires.seconds)
            consumer(ByteReadChannel(session))
        }
    }

    override suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit) {
        coroutineScope {
            val channel = writer(Dispatchers.Unconfined, autoFlush = true) {
                provider(channel)
            }.channel

            val key = id.withKeyPrefix()
            jedisPool.resource.use {
                it[key] = channel.toByteArray().toString(Charsets.UTF_8)
                it.expire(key, expires.seconds)
            }
        }
    }
}
