package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.model.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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

    override fun isAdmin(userId: Long): Boolean {
        return transaction {
            Admins
                .selectAll().where { Admins.user eq userId }
                .any()
        }
    }
}
