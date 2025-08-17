package com.github.hmiyado.kottage.model.validator

object StringValidator {
    private const val Japanese =
        "\\p{InHiragana}\\p{InKatakana}\\p{InCjkUnifiedIdeographs}\\p{InHalfwidthAndFullwidthForms}"
    private const val Numeric = "0-9"
    private const val Alphabet = "a-zA-Z"
    private const val Symbols = "\\-_"

    private val regexName = Regex("^[$Japanese$Alphabet][$Japanese$Numeric$Alphabet$Symbols]*$")
    fun validateName(target: String): Boolean {
        return regexName.matches(target)
    }
}
