package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryDatabase : UserRepository {
    override fun getUsers(): List<User> {
        return transaction {
            Users.selectAll().map { it.toUser() }
        }
    }

    override fun getUser(id: Long): User? {
        return transaction {
            Users.select { Users.id eq id }.firstOrNull()?.toUser()
        }
    }

    override fun createUser(screenName: String, password: String): User {
        return transaction {
            val id = Users.insertAndGetId {
                it[Users.screenName] = screenName
                it[Users.password] = password
            }
            Users.select { Users.id eq id }.first().toUser()
        }
    }

    private fun ResultRow.toUser(): User {
        return User(
            this[Users.id].value,
            this[Users.screenName]
        )
    }
}
