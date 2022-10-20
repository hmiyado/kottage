package com.github.hmiyado.kottage.route

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.IsStableType
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class PathKtTest : DescribeSpec({

    describe("assignPathParams") {
        withData(
            AssignPathParamsTestCase("/users/{id", listOf(1), "/users/{id"),
            AssignPathParamsTestCase("/users/id}", listOf(1), "/users/id}"),
            AssignPathParamsTestCase("/users/{id}", listOf(1), "/users/1"),
            AssignPathParamsTestCase("/entries/{id}/comments/{id}", listOf(1, 2), "/entries/1/comments/2"),
        ) { (template, params, expected) ->
            template.assignPathParams(*(params.toTypedArray())) shouldBe expected
        }
    }

    describe("matchesConcretePath") {
        withData(
            MatchesConcretePathTestCase("/users", "/users", true),
            MatchesConcretePathTestCase("/users/{id}", "/users/1", true),
            MatchesConcretePathTestCase("/users/1", "/users/1", true),
            MatchesConcretePathTestCase("/users", "/users/1", false),
        ) { (template, target, matches) ->
            template.matchesConcretePath(target) shouldBe matches
        }
    }
})

@IsStableType
data class AssignPathParamsTestCase(
    val template: String,
    val params: List<Any>,
    val expected: String,
) {
    override fun toString(): String {
        return "$template with params(${params.joinToString(",")}) should be $expected"
    }
}

data class MatchesConcretePathTestCase(
    val template: String,
    val target: String,
    val matches: Boolean,
) {
    override fun toString(): String {
        val shouldOrShouldNot = if (matches) {
            "should"
        } else {
            "should not"
        }
        return "$template $shouldOrShouldNot match as template to $target"
    }
}
