package com.github.hmiyado.kottage.route

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PathKtTest : DescribeSpec({
    describe("matchesConcretePath") {
        forAll<MatchesConcretePathTestCase>(
            arrayListOf(
                MatchesConcretePathTestCase("/users", "/users", true),
                MatchesConcretePathTestCase("/users/{id}", "/users/1", true),
                MatchesConcretePathTestCase("/users/1", "/users/1", true),
                MatchesConcretePathTestCase("/users", "/users/1", false),
            )
        ) { (template, target, matches) ->
            template.matchesConcretePath(target) shouldBe matches
        }
    }

})

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
