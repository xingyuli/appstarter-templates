package com.mobisist.springbootseed.appstarter.infra.web

import com.mobisist.springbootseed.appstarter.infra.service.Pageable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PageableDefault(val page: Int = 0, val size: Int = 10)

class PageableHandlerMethodArgumentResolver : HandlerMethodArgumentResolver {

    var fallbackPageable = Pageable(0, 10)
    var pageParameterName = "page"
    var sizeParameterName = "size"
    var qualifierDelimiter = "_"

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return Pageable::class.java == parameter.parameterType
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer,
                                 webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory): Pageable? {
        val defaultOrFallback = getDefaultFromAnnotationOrFallback(parameter)

        val pageString = webRequest.getParameter(getParameterNameToUse(pageParameterName, parameter))
        val sizeString = webRequest.getParameter(getParameterNameToUse(sizeParameterName, parameter))

        val page = if (!pageString.isNullOrEmpty()) {
            try { pageString.toInt() } catch (e: NumberFormatException) { 0 }
        } else {
            defaultOrFallback.page
        }
        val size = if (!sizeString.isNullOrEmpty()) {
            try { sizeString.toInt() } catch (e: NumberFormatException) { 0 }
        } else {
            defaultOrFallback.size
        }

        return Pageable(page, size)
    }

    private fun getParameterNameToUse(source: String, parameter: MethodParameter): String {
        if (parameter.hasParameterAnnotation(Qualifier::class.java)) {
            return "${parameter.getParameterAnnotation(Qualifier::class.java).value}$qualifierDelimiter$source"
        }
        return source
    }

    private fun getDefaultFromAnnotationOrFallback(parameter: MethodParameter): Pageable {
        if (parameter.hasParameterAnnotation(PageableDefault::class.java)) {
            val default = parameter.getParameterAnnotation(PageableDefault::class.java)
            return Pageable(default.page, default.size)
        }
        return fallbackPageable
    }

}

