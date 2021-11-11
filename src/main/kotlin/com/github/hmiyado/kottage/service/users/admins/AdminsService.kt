package com.github.hmiyado.kottage.service.users.admins

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.repository.users.admins.AdminRepository

interface AdminsService {
    fun addAdmin(user: User)

    fun removeAdmin(user: User)

    fun isAdmin(user: User): Boolean
}

class AdminsServiceImpl(private val adminRepository: AdminRepository) : AdminsService {
    override fun addAdmin(user: User) {
        adminRepository.addAdmin(user)
    }

    override fun removeAdmin(user: User) {
        adminRepository.removeAdmin(user)
    }

    override fun isAdmin(user: User): Boolean {
        return adminRepository.isAdmin(user)
    }
}
