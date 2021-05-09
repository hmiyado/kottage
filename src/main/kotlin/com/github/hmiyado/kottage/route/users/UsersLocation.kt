package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.post
import io.ktor.util.url

class UsersLocation {
    companion object {
        fun addRoute(route: Route, usersService: UsersService) = with(route) {
            get("/users") {
                val users = usersService.getUsers()
                call.respond(users)
            }
            post("/users") {
                val requestBody = kotlin.runCatching { call.receiveOrNull<Map<String, String>>() }.getOrNull()
                val (screenName, password) = listOf(requestBody?.get("screenName"), requestBody?.get("password"))
                if (screenName == null || password == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val user = try {
                    usersService.createUser(screenName, password)
                } catch (e: UsersService.DuplicateScreenNameException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                call.response.header("Location", this.context.url { this.path("users/${user.id}") })
                call.respond(HttpStatusCode.Created, user)
            }

            options("/users") {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
            }
        }
    }
}
