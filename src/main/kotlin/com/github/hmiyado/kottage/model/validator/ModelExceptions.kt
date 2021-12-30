package com.github.hmiyado.kottage.model.validator

data class InvalidParametersException(val parameters: List<String>) :
    IllegalArgumentException("${parameters.joinToString(",")} is invalid")
