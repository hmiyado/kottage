package com.github.hmiyado.repository.users

import com.github.hmiyado.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryDatabase : UserRepository {
    override fun getUser(id: Long): User? {
        return transaction {
            Users.select { Users.id eq id }.firstOrNull()?.toUser()
        }
    }

    private fun ResultRow.toUser(): User {
        return User(
            this[Users.id].value,
            this[Users.screenName]
        )
    }
}
