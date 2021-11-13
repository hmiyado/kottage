package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.route.Path
import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.options
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
@Location(Path.UsersId)
data class UsersIdLocation(val id: Long) {

    companion object {
        fun addRoute(route: Route, usersService: UsersService) = with(route) {
            get<UsersIdLocation> { location ->
                val user = usersService.getUser(location.id)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(user)
            }

            with(OpenApi) {
                usersIdPatch { (screenName), sessionUser ->
                    val pathUserId = call.parameters["id"]?.toLongOrNull()
                    if (sessionUser.id != pathUserId) {
                        // session user must match to user id in request path
                        call.respond(HttpStatusCode.Forbidden)
                        return@usersIdPatch
                    }
                    val updatedUser = usersService.updateUser(pathUserId, screenName)
                    if (updatedUser == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@usersIdPatch
                    }
                    call.respond(updatedUser)
                }
                usersIdDelete { sessionUser ->
                    val pathUserId = call.parameters["id"]?.toLongOrNull()
                    if (sessionUser.id != pathUserId) {
                        // session user must match to user id in request path
                        call.respond(HttpStatusCode.Forbidden)
                        return@usersIdDelete
                    }
                    usersService.deleteUser(pathUserId)
                    call.respond(HttpStatusCode.OK)
                }
            }

            options<UsersIdLocation> {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
            }
        }
    }
}
