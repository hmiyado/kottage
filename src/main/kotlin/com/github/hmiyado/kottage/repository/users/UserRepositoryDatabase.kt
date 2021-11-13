package com.github.hmiyado.kottage.repository.users

import com.github.hmiyado.kottage.model.Salt
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.Password
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserRepositoryDatabase : UserRepository {
    override fun getUsers(): List<User> {
        return transaction {
            Users.selectAll().map { it.toUser() }
        }
    }

    override fun findUserById(id: Long): User? {
        return transaction {
            Users.select { Users.id eq id }.firstOrNull()?.toUser()
        }
    }

    override fun findUserByScreenName(screenName: String): User? {
        return transaction {
            Users.select { Users.screenName eq id }.firstOrNull()?.toUser()
        }
    }

    override fun getUserWithCredentialsByScreenName(screenName: String): Triple<User, Password, Salt>? {
        return transaction {
            val user = Users
                .select { Users.screenName eq screenName }
                .firstOrNull()
                ?.toUser()
                ?: return@transaction null
            val (password, salt) = Passwords
                .select { Passwords.user eq user.id }
                .firstOrNull()
                ?.let {
                    Password(it[Passwords.password]) to Salt(it[Passwords.salt])
                } ?: return@transaction null
            Triple(user, password, salt)
        }
    }

    override fun createUser(screenName: String, password: String, salt: String): User {
        return transaction {
            val id = Users.insertAndGetId {
                it[Users.screenName] = screenName
            }
            Passwords.insert {
                it[user] = id
                it[Passwords.password] = password
                it[Passwords.salt] = salt
            }

            Users.select { Users.id eq id }.first().toUser()
        }
    }

    override fun updateUser(id: Long, screenName: String?): User? {
        return transaction {
            if (listOf(screenName).all { it == null }) {
                // if no property should update, just return current user
                return@transaction Users.select { Users.id eq id }.firstOrNull()?.toUser()
            }
            Users.update(where = { Users.id eq id }, limit = null) {
                if (screenName != null) {
                    it[Users.screenName] = screenName
                }
            }
            Users.select { Users.id eq id }.firstOrNull()?.toUser()
        }
    }

    override fun deleteUser(id: Long) {
        return transaction {
            Users.deleteWhere { Users.id eq id }
        }
    }

    companion object {
        fun ResultRow.toUser(): User {
            return User(
                this[Users.id].value,
                this[Users.screenName]
            )
        }
    }
}
