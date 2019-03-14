package com.github.hmiyado.route

import com.google.gson.Gson
import graphql.GraphQL
import io.ktor.application.call
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.ktor.ext.inject

fun Route.graphql() {
    val graphql: GraphQL by inject()

    post("/graphql") {
        val result = graphql.execute(call.receiveText())
        val specification = result.toSpecification()
        val resultAsJson = Gson().toJson(specification)
        call.respondText(resultAsJson.toString())
    }
}

