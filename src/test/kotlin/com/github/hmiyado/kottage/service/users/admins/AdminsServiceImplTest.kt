package com.github.hmiyado.kottage.service.users.admins

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.admins.AdminRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify

class AdminsServiceImplTest : DescribeSpec() {
    @MockK
    private lateinit var adminRepository: AdminRepository

    private lateinit var service: AdminsService

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        MockKAnnotations.init(this)
        service = AdminsServiceImpl(adminRepository)
    }

    init {
        describe("addAdmin") {
            it("should add user as admin") {
                val expected = User(id = 10)
                every { adminRepository.addAdmin(any()) } just runs
                service.addAdmin(expected)
                verify { adminRepository.addAdmin(expected) }
            }
        }

        describe("removeAdmin") {
            it("should remove user from admin") {
                val expected = User(id = 10)
                every { adminRepository.removeAdmin(any()) } just runs
                service.removeAdmin(expected)
                verify { adminRepository.removeAdmin(expected) }
            }
        }

        describe("isAdmin") {
            it("should recognize user as admin") {
                val admin = User(id = 10)
                val notAdmin = User(id = 55)
                every { adminRepository.isAdmin(admin.id) } returns true
                every { adminRepository.isAdmin(not(admin.id)) } returns false
                service.isAdmin(admin.id) shouldBe true
                service.isAdmin(notAdmin.id) shouldBe false
            }
        }
    }
}
