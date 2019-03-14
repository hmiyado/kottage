package com.github.hmiyado.module

import com.github.hmiyado.infra.graphql.ArticlesFetcher
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import org.koin.dsl.module
import java.io.File


val graphqlModule = module {
    single { SchemaParser() }
    single { get<SchemaParser>().parse(File("resources/graphql/schema.graphqls")) }
    single {
        RuntimeWiring
            .newRuntimeWiring()
            .type("QueryType") {
                it.dataFetcher("articles", ArticlesFetcher(get()))
            }
            .build()
    }
    single { SchemaGenerator() }
    single { get<SchemaGenerator>().makeExecutableSchema(get(), get()) }
    single { GraphQL.newGraphQL(get()).build() }
}