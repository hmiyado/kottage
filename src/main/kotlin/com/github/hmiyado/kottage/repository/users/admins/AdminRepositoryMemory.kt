package com.github.hmiyado.kottage.repository.users.admins

import com.github.hmiyado.kottage.model.User
import org.slf4j.LoggerFactory

class AdminRepositoryMemory : AdminRepository {
    private val logger = LoggerFactory.getLogger("Application")
    override fun addAdmin(user: User) {
        logger.debug("do nothing")
    }

    override fun removeAdmin(user: User) {
        logger.debug("do nothing")
    }

    override fun isAdmin(userId: Long): Boolean {
        val isAdmin = userId % 2 == 0L
        logger.debug("user id $userId is ${if (isAdmin) "" else "not"} admin")
        return isAdmin
    }
}
