package com.github.hmiyado.kottage.service.health

import com.github.hmiyado.kottage.model.Health
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HealthServiceImplTest : DescribeSpec() {
    private lateinit var service: HealthService
    private val version = Health.Version("v1.0.0")
    private val databaseType = Health.DatabaseType("database")

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        service = HealthServiceImpl(version, databaseType)
    }

    init {
        describe("getHealth") {
            it("should return OK") {
                service.getHealth() shouldBe Health("OK", version, databaseType)
            }
        }
    }

}
