package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.pathComponents
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.url
import com.github.hmiyado.kottage.openapi.models.User as ResponseUser


class UsersLocation {
    companion object {
        fun User.toResponseUser() = ResponseUser(screenName = screenName, id = id)

        fun addRoute(route: Route, usersService: UsersService) = with(route) {
            get(Path.Users) {
                val users = usersService.getUsers()
                call.respond(users)
            }

            with(OpenApi) {
                usersPost { (screenName, password) ->
                    val user = try {
                        usersService.createUser(screenName, password)
                    } catch (e: UsersService.DuplicateScreenNameException) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@usersPost
                    }
                    call.response.header("Location", this.context.url { this.pathComponents("/${user.id}") })
                    call.sessions.set(UserSession(id = user.id))
                    call.respond(HttpStatusCode.Created, user.toResponseUser())
                }

                usersCurrentGet { user ->
                    call.respond(HttpStatusCode.OK, user.toResponseUser())
                }

                signInPost { (screenName, password) ->
                    val user = usersService.authenticateUser(screenName, password)
                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@signInPost
                    }
                    call.sessions.set(UserSession(id = user.id))
                    call.respond(HttpStatusCode.OK, user.toResponseUser())
                }

                signOutPost {
                    call.sessions.clear<UserSession>()
                    call.respond(HttpStatusCode.OK)
                }
            }

            options(Path.Users) {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
            }
        }
    }
}
