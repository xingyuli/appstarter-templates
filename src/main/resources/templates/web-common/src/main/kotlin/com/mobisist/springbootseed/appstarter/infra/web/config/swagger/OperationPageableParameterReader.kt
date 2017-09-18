package com.mobisist.springbootseed.appstarter.infra.web.config.swagger

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Function
import com.mobisist.springbootseed.appstarter.infra.service.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.schema.ResolvedTypes
import springfox.documentation.spi.schema.contexts.ModelContext.inputParam
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.schema.ModelReference
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.Parameter

//@Component
//@Order(Ordered.LOWEST_PRECEDENCE)
open class OperationPageableParameterReader : OperationBuilderPlugin {

    @Autowired
    private lateinit var nameExtractor: TypeNameExtractor

    @Autowired
    private lateinit var typeResolver: TypeResolver

    private val pageableType: ResolvedType get() = typeResolver.resolve(Pageable::class.java)

    override fun apply(context: OperationContext) {
        val methodParameters = context.parameters
        val parameters = mutableListOf<Parameter>()

        for (methodParameter in methodParameters) {
            val resolvedType = methodParameter.parameterType

            if (pageableType == resolvedType) {
                val parameterContext = ParameterContext(methodParameter,
                        ParameterBuilder(),
                        context.documentationContext,
                        context.genericsNamingStrategy,
                        context)
                val factory = createModelRefFactory(parameterContext)

                val intModel = factory.apply(typeResolver.resolve(Integer.TYPE))

                parameters.add(ParameterBuilder()
                        .parameterType("query")
                        .name("page")
                        .modelRef(intModel)
                        .description("Results page you want to retrieve (0..N), default 0").build())
                parameters.add(ParameterBuilder()
                        .parameterType("query")
                        .name("size")
                        .modelRef(intModel)
                        .description("Number of records per page, default 10").build())
                context.operationBuilder().parameters(parameters)
            }
        }
    }

    override fun supports(delimiter: DocumentationType?): Boolean = true

    private fun createModelRefFactory(context: ParameterContext): Function<ResolvedType, out ModelReference> {
        val modelContext = inputParam(context.resolvedMethodParameter().parameterType.erasedType,
                context.documentationType,
                context.alternateTypeProvider,
                context.genericNamingStrategy,
                context.ignorableParameterTypes)
        return ResolvedTypes.modelRefFactory(modelContext, nameExtractor)
    }

}