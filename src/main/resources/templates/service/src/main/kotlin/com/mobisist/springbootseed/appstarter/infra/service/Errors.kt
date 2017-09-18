package com.mobisist.springbootseed.appstarter.infra.service

class ServiceException(val errorCode: ErrorCode) : RuntimeException("${errorCode.code}(${errorCode.message})")
class SMSServiceException(val errorCode: String) : RuntimeException(errorCode)
class ResourceNotFoundException(msg: String) : RuntimeException(msg)

interface ErrorCode {
    val code: Int
    val message: String
}

open class GroupedErrorCode(private val base: Int, private val from: Int, private val no: Int) : ErrorCode {
    final override val code: Int get() = base + from.mustGe("from", 0) + no.mustGe("no", 0)
    final override val message: String get() = "${javaClass.declaringClass.simpleName}.${javaClass.simpleName}"

    private fun Int.mustGe(name: String, n: Int) = if (this < n) throw IllegalArgumentException("$name must be greater than or equal to $n") else this
}

sealed class BuiltInErrorCode(from: Int, no: Int) : GroupedErrorCode(1000, from, no) {

    sealed class CommonErrorCode(no: Int) : BuiltInErrorCode(0, no) {
        object DATA_IN_USE : CommonErrorCode(0)
        object INTERNAL_ERROR : CommonErrorCode(1)
    }

    sealed class SmsErrorCode(no: Int) : BuiltInErrorCode(20, no) {
        object SEND_ERROR : SmsErrorCode(0)
        object VERIFICATION_ERROR : SmsErrorCode(1)
    }

}
