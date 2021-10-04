package com.github.hmiyado.kottage.route

object Path {
    private const val API_BASE = "/api"
    private const val API_V1 = "$API_BASE/v1"

    const val Entries = "$API_V1/entries"
    const val Health = "$API_V1/health"
    const val SignIn = "$API_V1/signIn"
    const val SignOut = "$API_V1/signOut"
    const val Users = "$API_V1/users"
}
