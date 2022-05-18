package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.openapi.models.Users
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.options
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.util.url
import com.github.hmiyado.kottage.openapi.models.User as ResponseUser


class UsersLocation(
    private val usersService: UsersService
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            usersGet {
                val users = usersService.getUsers()
                call.respond(Users(items = users.map { it.toResponseUser() }))
            }
            usersPost { (screenName, password) ->
                val user = try {
                    usersService.createUser(screenName, password)
                } catch (e: UsersService.DuplicateScreenNameException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@usersPost
                }
                call.response.header("Location", this.context.url { this.appendPathSegments("${user.id}") })
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
                val userSession = call.sessions.get<UserSession>()
                if (userSession?.id != null && userSession.id != user.id) {
                    // already signed in as another user
                    // this request is strange
                    call.respond(HttpStatusCode.Conflict, ErrorFactory.create409())
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

        route.options(Paths.usersGet) {
            call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Post)
        }
    }

    companion object {
        fun User.toResponseUser() = ResponseUser(screenName = screenName, id = id)
    }
}

fun ApplicationCall.findUser(usersService: UsersService): User? {
    val (userId) = sessions.get<UserSession>() ?: return null
    return usersService.getUser(userId)
}
