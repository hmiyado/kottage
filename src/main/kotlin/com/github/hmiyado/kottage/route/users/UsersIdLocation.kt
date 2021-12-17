package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.options

class UsersIdLocation() {

    companion object {
        fun addRoute(route: Route, usersService: UsersService) = with(route) {
            with(OpenApi) {
                usersIdGet {
                    val userId = call.usersIdGetId()
                    val user = usersService.getUser(userId)
                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@usersIdGet
                    }
                    call.respond(user)
                }
                usersIdPatch { (screenName), sessionUser ->
                    val pathUserId = call.usersIdPatchId()
                    if (sessionUser.id != pathUserId) {
                        // session user must match to user id in request path
                        call.respond(HttpStatusCode.Forbidden)
                        return@usersIdPatch
                    }
                    val updatedUser = try {
                        usersService.updateUser(pathUserId, screenName)
                    } catch (e: UsersService.DuplicateScreenNameException) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@usersIdPatch
                    }
                    if (updatedUser == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@usersIdPatch
                    }
                    call.respond(updatedUser)
                }
                usersIdDelete { sessionUser ->
                    val pathUserId = call.usersIdDeleteId()
                    if (sessionUser.id != pathUserId) {
                        // session user must match to user id in request path
                        call.respond(HttpStatusCode.Forbidden)
                        return@usersIdDelete
                    }
                    usersService.deleteUser(pathUserId)
                    call.respond(HttpStatusCode.OK)
                }
            }

            options(Paths.usersIdGet) {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
            }
        }
    }
}
