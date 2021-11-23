package com.github.hmiyado.kottage.route

object Path {
    const val Root = "/"

    private const val API_BASE = "/api"
    private const val API_V1 = "$API_BASE/v1"

    const val Entries = "$API_V1/entries"
    const val EntriesSerialNumber = "$API_V1/entries/{serialNumber}"
    const val Health = "$API_V1/health"
    const val Users = "$API_V1/users"
    const val UsersId = "$API_V1/users/{id}"
}

/**
 * "/users/{id}".assignPathParams("id" to 1) returns "/users/1"
 */
fun String.assignPathParams(vararg params: Pair<String, Any>): String {
    return params.map { (k, v) -> "{$k}" to v.toString() }.fold(this) { acc, (k, v) -> acc.replace(k, v) }
}

fun String.assignPathParams(vararg params: Any): String {
    return this
        .split("/")
        .fold("" to params.toList().map { it.toString() }) { (accPath, restParams), currentPath ->
            val (newSubPath, newRestParams) = if (currentPath.startsWith("{") && currentPath.endsWith("}")) {
                restParams.first() to restParams.drop(1)
            } else {
                currentPath to restParams
            }
            "${accPath}/${newSubPath}" to newRestParams
        }
        .first
}
