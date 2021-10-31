package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.models.SignInPostRequest
import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.route.receiveOrThrow
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
import io.ktor.routing.post
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.url

class UsersLocation {
    companion object {
        fun addRoute(route: Route, usersService: UsersService) = with(route) {
            get(Path.Users) {
                val users = usersService.getUsers()
                call.respond(users)
            }
            post(Paths.usersPost) {
                val (screenName, password) = call.receiveOrThrow<UsersRequestPayload.Post>()
                val user = try {
                    usersService.createUser(screenName, password)
                } catch (e: UsersService.DuplicateScreenNameException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                call.response.header("Location", this.context.url { this.pathComponents("/${user.id}") })
                call.sessions.set(UserSession(id = user.id))
                call.respond(HttpStatusCode.Created, user)
            }

            post(Paths.signInPost) {
                val (screenName, password) = call.receiveOrThrow<SignInPostRequest>()
                val user = usersService.authenticateUser(screenName, password)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
                call.sessions.set(UserSession(id = user.id))
                call.respond(HttpStatusCode.OK, user)
            }

            post(Path.SignOut) {
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.OK)
            }

            options(Path.Users) {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
            }
        }
    }
}
