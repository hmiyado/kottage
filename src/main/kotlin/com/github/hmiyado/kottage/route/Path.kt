package com.github.hmiyado.kottage.route

object Path {
    const val Root = "/"

    private const val API_BASE = "/api"
    private const val API_V1 = "$API_BASE/v1"

    const val Entries = "$API_V1/entries"
    const val EntriesSerialNumber = "$API_V1/entries/{serialNumber}"
    const val Health = "$API_V1/health"
    const val SignIn = "$API_V1/signIn"
    const val SignOut = "$API_V1/signOut"
    const val Users = "$API_V1/users"
    const val UsersId = "$API_V1/{id}"
}
