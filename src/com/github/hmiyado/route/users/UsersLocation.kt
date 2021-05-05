package com.github.hmiyado.route.users

import com.github.hmiyado.route.allowMethods
import com.github.hmiyado.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options

class UsersLocation {
    companion object {
        fun addRoute(route: Route, usersService: UsersService) = with(route) {
            get("/users") {
                val users = usersService.getUsers()
                call.respond(users)
            }

            options("/users") {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
            }
        }
    }
}
