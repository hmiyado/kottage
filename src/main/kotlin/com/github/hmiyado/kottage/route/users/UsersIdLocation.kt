package com.github.hmiyado.kottage.route.users

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
            options<UsersIdLocation> {
                call.response.allowMethods(HttpMethod.Options, HttpMethod.Get)
            }
        }
    }
}
