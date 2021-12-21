package com.github.hmiyado.kottage.route

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
