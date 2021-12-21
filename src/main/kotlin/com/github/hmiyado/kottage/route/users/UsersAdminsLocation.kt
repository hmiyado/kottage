package com.github.hmiyado.kottage.route.users

import com.github.hmiyado.kottage.openapi.apis.OpenApi
import com.github.hmiyado.kottage.openapi.models.Admin
import com.github.hmiyado.kottage.openapi.models.AdminsGetResponse
import com.github.hmiyado.kottage.route.Router
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route

class UsersAdminsLocation(
    private val usersService: UsersService,
    private val adminsService: AdminsService,
) : Router {
    override fun addRoute(route: Route) {
        with(OpenApi(route)) {
            usersAdminsGet {
                val response = usersService.getUsers()
                    .filter { adminsService.isAdmin(it.id) }
                    .map { Admin(it.id) }
                    .let { AdminsGetResponse(it) }
                call.respond(HttpStatusCode.OK, response)
            }
            usersAdminsPatch { payload, _ ->
                val target = usersService.getUser(payload.id) ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@usersAdminsPatch
                }
                if (adminsService.isAdmin(target.id)) {
                    // target is already admin
                    call.respond(HttpStatusCode.OK)
                    return@usersAdminsPatch
                }
                adminsService.addAdmin(target)
                call.respond(HttpStatusCode.OK)
            }
            usersAdminsDelete { payload, _ ->
                val target = usersService.getUser(payload.id) ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@usersAdminsDelete
                }
                if (adminsService.isAdmin(target.id)) {
                    adminsService.removeAdmin(target)
                    call.respond(HttpStatusCode.OK)
                    return@usersAdminsDelete
                }
                // target is already not admin
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
