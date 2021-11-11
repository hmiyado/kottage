package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.model.User
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AdminRepositoryDatabase : AdminRepository {
    override fun addAdmin(user: User) {
        transaction {
            Admins.insert { it[Admins.user] = user.id }
        }
    }

    override fun removeAdmin(user: User) {
        transaction {
            Admins.deleteWhere { Admins.user eq user.id }
        }
    }

    override fun isAdmin(user: User): Boolean {
        return transaction {
            Admins
                .select { Admins.user eq user.id }
                .any()
        }
    }

}
