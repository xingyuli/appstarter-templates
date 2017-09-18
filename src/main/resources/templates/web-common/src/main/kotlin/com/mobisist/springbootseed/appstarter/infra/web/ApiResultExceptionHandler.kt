package com.mobisist.springbootseed.appstarter.infra.web

import com.mobisist.springbootseed.appstarter.infra.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

open class ApiResultExceptionHandler : ResponseEntityExceptionHandler() {

    @Autowired
    private lateinit var messageSource: MessageSource

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<String> {
        logger.info("resource was not found", e)
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(e: ServiceException): ResponseEntity<ApiResult<*>> {
        val appApiMessage = messageSource.getMessage(e.errorCode.message, null, null)
        logger.info(e.errorCode, e)
        return ApiResult.ofCode(e.errorCode, appApiMessage)
    }

    @ExceptionHandler(SMSServiceException::class)
    fun handleServiceException(e: SMSServiceException): ResponseEntity<ApiResult<*>> {
        val appApiMessage = messageSource.getMessage(e.errorCode, null, null)
        logger.info(e.errorCode, e)
        return ApiResult.ofCode(BuiltInErrorCode.SmsErrorCode.SEND_ERROR, appApiMessage)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        logger.info(e.message, e)
        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException, req: WebRequest): ResponseEntity<Any> {
        logger.info(e.message, e)
        return handleExceptionInternal(e, null, null, HttpStatus.UNAUTHORIZED, req)
    }

}