package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.model.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class AdminRepositoryDatabase : AdminRepository {
    override fun addAdmin(user: User) {
        transaction {
            Admins.insert { it[Admins.user] = user.id }
        }
    }

    override fun removeAdmin(user: User) {
        transaction {
            Admins.deleteWhere { this.user eq user.id }
        }
    }

    override fun isAdmin(userId: Long): Boolean =
        transaction {
            Admins
                .selectAll()
                .where { Admins.user eq userId }
                .any()
        }
}
