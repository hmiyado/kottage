package com.github.hmiyado.kottage.repository.users

import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    val screenName = varchar("screenName", 100)
}
