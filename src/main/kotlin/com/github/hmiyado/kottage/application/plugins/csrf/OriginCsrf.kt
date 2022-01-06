//package com.github.hmiyado.kottage.application.plugins.csrf
//
//import io.ktor.application.ApplicationCall
//
//private typealias CsrfOriginValidatorFunction = (header: String, values: List<String>) -> Boolean
//private typealias HeaderCsrfOnFailFunction = suspend ApplicationCall.() -> Unit
//
//
//class OriginCsrfProvider private constructor(
//
//) : CsrfProvider() {
//
//    class Configuration : CsrfProvider.Configuration() {
//        var onFail: HeaderCsrfOnFailFunction = {}
//
//        var validator: CsrfHeaderValidatorFunction = { _, _ -> false }
//
//        fun validator(validator: CsrfHeaderValidatorFunction) {
//            this.validator = validator
//        }
//
//        fun onFail(block: HeaderCsrfOnFailFunction) {
//            onFail = block
//        }
//
//        fun buildProvider(): HeaderCsrfProvider {
//            return HeaderCsrfProvider(this)
//        }
//
//    }
//}
