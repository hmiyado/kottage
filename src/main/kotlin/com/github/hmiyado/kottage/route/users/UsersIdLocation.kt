package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.route.allowMethods
import com.github.hmiyado.kottage.service.users.UsersService
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.delete
import io.ktor.locations.get
import io.ktor.locations.options
import io.ktor.locations.patch
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
@Location("/users/{id}")
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

            patch<UsersIdLocation> { location ->
                val requestBody = kotlin.runCatching { call.receiveOrNull<Map<String, String>>() }.getOrNull()
                val screenName = requestBody?.get("screenName")
                if (screenName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }
                val user = usersService.updateUser(location.id, screenName)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@patch
                }
                call.respond(user)
            }

            delete<UsersIdLocation> { location ->
                usersService.deleteUser(location.id)
                call.respond(HttpStatusCode.OK)
            }

            options<UsersIdLocation> {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get, HttpMethod.Patch, HttpMethod.Delete)
            }
        }
    }
}
