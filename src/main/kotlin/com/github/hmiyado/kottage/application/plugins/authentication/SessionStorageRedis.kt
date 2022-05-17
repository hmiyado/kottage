package com.github.hmiyado.kottage.application.plugins.authentication

import io.ktor.server.sessions.SessionStorage
import java.time.Duration
import kotlinx.coroutines.coroutineScope
import redis.clients.jedis.JedisPool

class SessionStorageRedis(
    private val jedisPool: JedisPool,
    private val keyPrefix: String,
    private val expires: Duration
) : SessionStorage {
    private fun String.withKeyPrefix() = "$keyPrefix:$this"

    override suspend fun invalidate(id: String) {
        jedisPool.resource.use {
            it.del(id.withKeyPrefix())
        }
    }

    override suspend fun read(id: String): String {
        val key = id.withKeyPrefix()
        return jedisPool.resource.use {
            val session = it[key] ?: throw NoSuchElementException("Session $id not found")
            it.expire(key, expires.seconds)
            session
        }
    }

    override suspend fun write(id: String, value: String) {
        coroutineScope {
            val key = id.withKeyPrefix()
            jedisPool.resource.use {
                it[key] = value
                it.expire(key, expires.seconds)
            }
        }
    }
}
