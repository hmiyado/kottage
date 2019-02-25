package com.github.hmiyado

import com.github.hmiyado.infra.graphql.ArticlesFetcher
import com.github.hmiyado.infra.repositoryimplementation.ArticleRepositoryMock
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import java.io.File

val graphql: GraphQL by lazy {
    val schemaParser = SchemaParser()
    val typeDefinitionRegistry = schemaParser.parse(File("resources/graphql/schema.graphqls"))

    val runtimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("QueryType") {
            it.dataFetcher("articles", ArticlesFetcher(ArticleRepositoryMock()))
        }
        .build()

    val schemaGenerator = SchemaGenerator()
    val schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
    GraphQL.newGraphQL(schema).build()
}
