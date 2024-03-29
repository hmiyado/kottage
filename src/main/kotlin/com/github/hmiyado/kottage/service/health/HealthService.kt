package com.github.hmiyado.kottage.service.health

import com.github.hmiyado.kottage.model.Health

interface HealthService {
    fun getHealth(): Health
}

class HealthServiceImpl(
    private val version: Health.Version,
    private val databaseType: Health.DatabaseType,
) : HealthService {
    override fun getHealth(): Health {
        return Health("OK", version, databaseType)
    }
}
