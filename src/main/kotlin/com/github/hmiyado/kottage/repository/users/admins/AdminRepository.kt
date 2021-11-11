package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.model.User

interface AdminRepository {
    fun addAdmin(user: User)

    fun removeAdmin(user: User)

    fun isAdmin(user: User): Boolean
}
