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
        .filter { it.isNotEmpty() }
        .fold("" to params.toList().map { it.toString() }) { (accPath, restParams), currentPath ->
            val (newSubPath, newRestParams) = if (currentPath.startsWith("{") && currentPath.endsWith("}")) {
                restParams.first() to restParams.drop(1)
            } else {
                currentPath to restParams
            }
            "$accPath/$newSubPath" to newRestParams
        }
        .first
}

/**
 * matches path template and concrete path
 * for example,
 * - "/users".matchAsConcretePath("/users") // => true
 * - "/users/{id}".matchAsConcretePath("/users/1") // => true
 * - "/users/1".matchAsConcretePath("/users/{id}") // => false
 * - "/users".matchAsConcretePath("/users/1") // => false
 */
fun String.matchesConcretePath(path: String): Boolean {
    val thisPaths = this.split("/")
    val thatPaths = path.split("/")
    if (thisPaths.size != thatPaths.size) {
        return false
    }

    return thisPaths
        .zip(thatPaths)
        .all { (thisPath, thatPath) ->
            if (thisPath.startsWith("{") && thisPath.endsWith("}")) {
                return@all true
            }
            thisPath == thatPath
        }
}
