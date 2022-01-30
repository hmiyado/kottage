package com.github.hmiyado.kottage.model.validator

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class StringValidatorTest : DescribeSpec() {

    init {
        describe("validateName") {
            withData(
                nameFn = { (target, isValid) -> "$target is ${if (isValid) "valid" else "invalid"}" },
                arrayListOf(
                    "name" to true,
                    "onlyAlphabet" to true,
                    "全角英数Ａ１" to true,
                    "ひらがな" to true,
                    "カタカナ" to true,
                    "ﾊﾝｶｸｶﾀｶﾅ" to true,
                    "漢字" to true,
                    "有効なモジ_Ａll-ﾃｽﾄ012３ー＿" to true,
                    "_記号から始まるのはNG" to false,
                    "-記号から始まるのはNG" to false,
                    "0数字から始まるのはNG" to false,
                )
            ) { (target, isValid) ->
                StringValidator.validateName(target) shouldBe isValid
            }
        }
    }
}
