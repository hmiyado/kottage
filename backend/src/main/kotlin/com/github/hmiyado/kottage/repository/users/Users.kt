package com.github.hmiyado.kottage.repository.users

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Users : LongIdTable() {
    val screenName = varchar("screenName", 100).uniqueIndex()
}
