package com.github.hmiyado.kottage.model.validator

object StringValidator {
    private const val JAPANESE =
        "\\p{InHiragana}\\p{InKatakana}\\p{InCjkUnifiedIdeographs}\\p{InHalfwidthAndFullwidthForms}"
    private const val NUMERIC = "0-9"
    private const val ALPHABET = "a-zA-Z"
    private const val SYMBOLS = "\\-_"

    private val regexName = Regex("^[$JAPANESE$ALPHABET][$JAPANESE$NUMERIC$ALPHABET$SYMBOLS]*$")

    fun validateName(target: String): Boolean = regexName.matches(target)
}
