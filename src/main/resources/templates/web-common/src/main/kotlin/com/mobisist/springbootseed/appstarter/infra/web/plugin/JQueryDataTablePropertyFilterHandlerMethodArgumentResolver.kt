package com.mobisist.springbootseed.appstarter.infra.web.plugin

import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PropertyFilters

class JQueryDataTablePropertyFilterHandlerMethodArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(PropertyFilters::class.java)
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer,
                                 webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory): List<PropertyFilter> {
        val propertyFiltersKey = webRequest.parameterMap
                .filterKeys { it.startsWith("columns") }
                .keys.trimmedGroup()

        val filters = propertyFiltersKey.values.map {
            val filter = PropertyFilter()
            val searchParameterNames = mutableListOf<ProcessedParameterName>()
            it.forEach {
                when (it.current.firstKey()) {
                    "data" -> filter.data = webRequest.getParameter(it.fullName) ?: ""
                    "name" -> filter.name = webRequest.getParameter(it.fullName) ?: ""
                    "search" -> searchParameterNames.add(ProcessedParameterName(it.current.substringAfter("[search]"), it.fullName))
                    else -> { /* no op */ }
                }

                val searchCondition = SearchCondition()
                searchParameterNames.forEach {
                    when (it.current.firstKey()) {
                        "value" -> searchCondition.value = webRequest.getParameter(it.fullName)
                        "regex" -> searchCondition.regex = webRequest.getParameter(it.fullName).toBoolean()
                    }
                }
                filter.search = searchCondition
            }
            filter
        }

        return filters
    }

    /**
     * Transform given parameter keys from
     *
     * ```
     * [
     *   'columns[0][data]'
     *   'columns[0][name]',
     *   'columns[0][searchable]',
     *   'columns[0][orderable]',
     *   'columns[0][search][value]',
     *   'columns[0][search][regex]',
     *   'columns[1][data]',
     *   'columns[1][name]',
     *   'columns[1][searchable]',
     *   'columns[1][orderable]',
     *   'columns[1][search][value]',
     *   'columns[1][search][regex]'
     * ]
     * ```
     *
     * to
     *
     * ```
     * {
     *   '0': [
     *     { current: '[data]',          fullName: 'columns[0][data]' },
     *     { current: '[name]',          fullName: 'columns[0][name]' },
     *     { current: '[searchable]',    fullName: 'columns[0][searchable]' },
     *     { current: '[orderable]',     fullName: 'columns[0][orderable]' },
     *     { current: '[search][value]', fullName: 'columns[0][search][value]' },
     *     { current: '[search][regex]', fullName: 'columns[0][search][regex]' },
     *   ],
     *   '1': [
     *     { current: '[data]',          fullName: 'columns[1][data]' },
     *     { current: '[name]',          fullName: 'columns[1][name]' },
     *     { current: '[searchable]',    fullName: 'columns[1][searchable]' },
     *     { current: '[orderable]',     fullName: 'columns[1][orderable]' },
     *     { current: '[search][value]', fullName: 'columns[1][search][value]' },
     *     { current: '[search][regex]', fullName: 'columns[1][search][regex]' }
     *   ]
     * }
     * ```
     */
    private fun Collection<String>.trimmedGroup(): Map<String, List<ProcessedParameterName>>
            = groupBy { it.firstKey() }.mapValues { (key, group) -> group.map { ProcessedParameterName(it.substringAfter("[$key]"), it) } }

    private fun String.firstKey(): String {
        val openSquareBracket = indexOf("[")
        val closeSquareBracket = indexOf("]")
        return substring(openSquareBracket + 1, closeSquareBracket)
    }

    private data class ProcessedParameterName(val current: String, val fullName: String)

}