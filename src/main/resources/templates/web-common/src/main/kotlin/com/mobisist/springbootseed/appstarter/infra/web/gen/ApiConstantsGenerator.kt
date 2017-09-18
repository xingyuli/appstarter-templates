package com.mobisist.springbootseed.appstarter.infra.web.gen

import com.mobisist.springbootseed.appstarter.common.camelCaseToUnderscoreSeparated
import com.mobisist.springbootseed.appstarter.common.mustAnnotation
import com.mobisist.springbootseed.appstarter.infra.service.BuiltInErrorCode
import com.mobisist.springbootseed.appstarter.infra.service.GroupedErrorCode
import com.mobisist.springbootseed.appstarter.infra.web.ApiCode
import com.mobisist.springbootseed.appstarter.infra.web.ApiResult
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.swordess.common.lang.Classes
import org.swordess.common.lang.withPrefix
import org.swordess.common.lang.withoutSuffix
import java.lang.reflect.Method
import kotlin.reflect.KClass

class ApiConstantsGenerator(config: ApiConstantsGeneratorConfig) : JavascriptGenerator(config) {

    override fun doGenerate() {
        +"var CON = window.CON || {};"
        +"var contextRoot = window.contextRoot || '';"
        generateApiAndPage()
        generateGroupedMessageCode()
    }

    private fun generateApiAndPage() {
        val handlerMethods = mutableListOf<RequestHandler>()
        Classes.underPackage((config as ApiConstantsGeneratorConfig).controllerPackage!!) {
            it.java.annotations.firstOrNull { it is Controller || it is RestController } != null
        }.forEach {
            it.java.methods.forEach { method ->
                MappingAnnotation.findFirstOrNull(method)?.let {
                    handlerMethods.add(RequestHandler(method, it))
                }
            }
        }

        with(handlerMethods.filter { it.rest }.sortedBy { it.pathName }) {
            +""
            +"/* path for REST API */"
            +"CON.API = {};"
            forEach {
                +"CON.API.${it.pathName} = contextRoot + '${it.pathValue}';"
            }
        }
        with(handlerMethods.filter { !it.rest }.sortedBy { it.pathName }) {
            +""
            +"/* path for page navigation */"
            +"CON.PAGE = {};"
            forEach {
                +"CON.PAGE.${it.pathName} = contextRoot + '${it.pathValue}';"
            }
        }
    }

    private fun generateGroupedMessageCode() {
        +""
        +"CON.CODE = {};"

        with(collectCommonErrCodes()) {
            +""
            +"/* common error code */"
            forEach {
                +"CON.CODE.${it.value.javaClass.simpleName} = ${it.key};"
            }
        }

        val presentedCodes = mutableMapOf<Int, GroupedErrorCode>()
        with(collectGroupedErrCodes(BuiltInErrorCode::class, presentedCodes)) {
            +""
            +"/* built-in error code */"
            forEach { group ->
                +"CON.CODE.${group.value[0].javaClass.declaringClass.simpleName} = {};"
                group.value.forEach {
                    +"CON.CODE.${it.message} = ${it.code};"
                }
            }
        }

        val customErrorCodeClass: Class<GroupedErrorCode> = Class.forName((config as ApiConstantsGeneratorConfig).customErrorCodeClass) as Class<GroupedErrorCode>
        with(collectGroupedErrCodes(customErrorCodeClass.kotlin, presentedCodes)) {
            +""
            +"/* custom error code */"
            forEach { group ->
                +"CON.CODE.${group.value[0].javaClass.declaringClass.simpleName} = {};"
                group.value.forEach {
                    +"CON.CODE.${it.message} = ${it.code};"
                }
            }
        }
    }

    internal class RequestHandler(val handler: Method, val mappingAnnotation: MappingAnnotation) {

        init {
            if (handler.returnType == ApiResult::class.java
                    && (!handler.declaringClass.isAnnotationPresent(RestController::class.java) && !handler.isAnnotationPresent(ResponseBody::class.java))) {
                throw IllegalArgumentException("neither @RestController nor @ResponseBody was found on ${handler.declaringClass.name}.${handler.name}")
            }
        }

        val rest: Boolean = !isNotRestHandler()

        val pathName: String by lazy {
            val transformedClassName = handler.declaringClass.simpleName.withoutSuffix("Controller")
            val transformedMethodName = handler.name.let {
                var transformed = it
                if (it.startsWith("to") && it.length > 2 && !it[2].isLowerCase()) {
                    transformed = transformed.withPrefix("to")
                }
                transformed.withoutSuffix("Page")
            }

            (transformedClassName + "_" + transformedMethodName.camelCaseToUnderscoreSeparated()).toUpperCase()
        }

        val pathValue: String by lazy {
            val classLevelMapping = handler.declaringClass.getAnnotation(RequestMapping::class.java)
            val classLevelPath = classLevelMapping?.let {
                with(it.value) {
                    if (isNotEmpty()) this[0] else ""
                }
            } ?: ""

            val methodLevelPath = with(mappingAnnotation.pathValueOf(handler)) {
                if (isNotEmpty()) this[0] else ""
            }

            classLevelPath.withPrefix("/").withoutSuffix("/") + methodLevelPath.withPrefix("/")
        }

        private fun isNotRestHandler(): Boolean {
            return ModelAndView::class.java.isAssignableFrom(handler.returnType)
                    || (!handler.declaringClass.isAnnotationPresent(RestController::class.java) && !handler.isAnnotationPresent(ResponseBody::class.java))
        }

    }

    enum class MappingAnnotation {

        REQUEST_MAPPING {
            override fun isPresentOn(method: Method) = method.isAnnotationPresent(RequestMapping::class.java)
            override fun pathValueOf(method: Method) = method.mustAnnotation(RequestMapping::class.java).value
        },

        GET_MAPPING {
            override fun isPresentOn(method: Method) = method.isAnnotationPresent(GetMapping::class.java)
            override fun pathValueOf(method: Method) = method.mustAnnotation(GetMapping::class.java).value
        },

        POST_MAPPING {
            override fun isPresentOn(method: Method) = method.isAnnotationPresent(PostMapping::class.java)
            override fun pathValueOf(method: Method) = method.mustAnnotation(PostMapping::class.java).value
        },

        PUT_MAPPING {
            override fun isPresentOn(method: Method) = method.isAnnotationPresent(PutMapping::class.java)
            override fun pathValueOf(method: Method) = method.mustAnnotation(PutMapping::class.java).value
        },

        DELETE_MAPPING {
            override fun isPresentOn(method: Method) = method.isAnnotationPresent(DeleteMapping::class.java)
            override fun pathValueOf(method: Method) = method.mustAnnotation(DeleteMapping::class.java).value
        },

        PATCH_MAPPING {
            override fun isPresentOn(method: Method) = method.isAnnotationPresent(PatchMapping::class.java)
            override fun pathValueOf(method: Method) = method.mustAnnotation(PatchMapping::class.java).value
        };

        abstract fun isPresentOn(method: Method): Boolean
        abstract fun pathValueOf(method: Method): Array<String>

        companion object {

            fun isAnyPresentOn(method: Method): Boolean = findFirstOrNull(method) != null

            fun findFirstOrNull(method: Method): MappingAnnotation? = values().find { it.isPresentOn(method) }

        }

    }

    private fun collectCommonErrCodes(): Map<Int, ApiCode> {
        val commonErrCodes = sortedMapOf<Int, ApiCode>()
        ApiCode::class.nestedClasses.forEach {
            val instance = it.objectInstance
            if (instance != null && instance is ApiCode) {
                commonErrCodes.put(instance.code, instance)
            }
        }
        return commonErrCodes
    }

    private fun <T : GroupedErrorCode> collectGroupedErrCodes(grouper: KClass<T>, presentedCodes: MutableMap<Int, GroupedErrorCode>): Map<Int, List<T>> {
        val groupedErrCodes = sortedMapOf<Int, List<T>>()

        grouper.nestedClasses.forEach { mayBeSealedClass ->

            // filter out sealed class
            if (mayBeSealedClass.objectInstance == null && grouper.java.isAssignableFrom(mayBeSealedClass.java)) {
                val errCodesInGroup = mutableListOf<T>()
                mayBeSealedClass.nestedClasses.forEach { mayBeErrorCode ->

                    // filter out error code
                    val instance = mayBeErrorCode.objectInstance
                    if (instance != null && mayBeSealedClass.java.isInstance(instance)) {

                        @Suppress("unchecked_cast")
                        val errorCode = instance as T
                        if (presentedCodes.contains(errorCode.code)) {
                            throw IllegalArgumentException("code of ${errorCode.message} conflicted with ${presentedCodes[errorCode.code]!!.message}, both have value ${errorCode.code}")
                        }
                        presentedCodes.put(errorCode.code, errorCode)
                        errCodesInGroup.add(errorCode)
                    }
                }

                if (errCodesInGroup.isNotEmpty()) {
                    groupedErrCodes.put(errCodesInGroup.sortedBy { it.code }[0].code, errCodesInGroup)
                }
            }
        }

        return groupedErrCodes
    }

}
