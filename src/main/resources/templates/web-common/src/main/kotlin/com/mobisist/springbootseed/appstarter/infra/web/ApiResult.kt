package com.mobisist.springbootseed.appstarter.infra.web

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mobisist.springbootseed.appstarter.infra.service.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class ApiCode(val code: Int) {
    object OK : ApiCode(200)
    class AppApiCode(errorCode: ErrorCode) : ApiCode(errorCode.code)
}

class ApiResult<out T> private constructor(@JsonIgnore val apiCode: ApiCode, val result: T? = null, val message: String? = null) {

    fun getCode(): Int = apiCode.code

    companion object {

        /**
         * @return a `200` [ResponseEntity] with its body set to an [ApiResult] with result set to `null`.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> ok(): ResponseEntity<ApiResult<T>> = ofCode(ApiCode.OK) as ResponseEntity<ApiResult<T>>

        /**
         * @return a `400` [ResponseEntity] with its body set to `null`.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> badRequest(): ResponseEntity<ApiResult<T>> = ResponseEntity.badRequest().build()

        /**
         * @return a `403` [ResponseEntity] with its body set to `null`
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> forbidden(): ResponseEntity<ApiResult<T>> = ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        /**
         * @return a `404` [ResponseEntity] with its body set to `null`
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> notFound(): ResponseEntity<ApiResult<T>> = ResponseEntity.notFound().build()

        /**
         * @return a `423` [ResponseEntity] with its body set to `null`
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> locked(): ResponseEntity<ApiResult<T>> = ResponseEntity.status(HttpStatus.LOCKED).build()

        /**
         * Returns a `200` [ResponseEntity] with its body set to an [ApiResult] with specified non-null result.
         *
         * @param result the result to be set on the [ApiResult], which must be non-null
         * @return a `200` [ResponseEntity]
         */
        @JvmStatic
        fun <T> of(result: T): ResponseEntity<ApiResult<T>> = ResponseEntity.ok(ApiResult(ApiCode.OK, result = result))

        /**
         * Returns a `200` [ResponseEntity] with its body set to an [ApiResult] with specified result, if `result` is
         * non-null, otherwise returns a `404`.
         *
         * This is just a combination of [of] and [notFound].
         *
         * @param result the possibly-null result to set
         * @return a `200` [ResponseEntity] if the specified result is non-null, otherwise a `404`
         */
        @JvmStatic
        fun <T> ofNullable(result: T?): ResponseEntity<ApiResult<T>> = if (result == null) notFound<T>() else of(result)

        /**
         * Returns a `200` [ResponseEntity] with its body set to an [ApiResult] with specified code and message.
         *
         * @param apiCode the code to be set on the [ApiResult]
         * @param message the message to be set on the [ApiResult]
         * @return a `200` [ResponseEntity] with its body set to an [ApiResult] with specified code and message
         */
        @JvmStatic
        @JvmOverloads
        fun ofCode(apiCode: ApiCode, message: String? = null): ResponseEntity<ApiResult<*>> = ResponseEntity.ok(ApiResult(apiCode, result = null, message = message))

        /**
         * Returns a `200` [ResponseEntity] with its body set to an [ApiResult] with specified code and message.
         *
         * @param apiCode the code to be set on the [ApiResult]
         * @param message the message to be set on the [ApiResult]
         * @return a `200` [ResponseEntity] with its body set to an [ApiResult] with specified code and message
         */
        @JvmStatic
        @JvmOverloads
        fun ofCode(errorCode: ErrorCode, message: String? = null): ResponseEntity<ApiResult<*>> = ofCode(apiCode = ApiCode.AppApiCode(errorCode), message = message)

    }

}