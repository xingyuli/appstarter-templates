package com.mobisist.springbootseed.appstarter.infra.web.config.swagger

import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import springfox.bean.validators.plugins.*
import springfox.bean.validators.plugins.BeanValidators.validatorFromBean
import springfox.bean.validators.plugins.BeanValidators.validatorFromField
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.schema.contexts.ModelPropertyContext
import javax.validation.constraints.NotNull

/**
 * Copy of springfox's built-in BeanValidatorPluginsConfiguration except that we improved the NotNull recognition.
 */
//@Configuration
open class SwaggerBeanValidatorPluginsConfiguration {

    @Bean
    open fun minMaxPlugin() = MinMaxAnnotationPlugin()

    @Bean
    open fun decimalMinMaxPlugin() = DecimalMinMaxAnnotationPlugin()

    @Bean
    open fun sizePlugin() = SizeAnnotationPlugin()

    @Bean
    open fun notNullPlugin() = NotNullAnnotationPlugin()

    @Bean
    open fun patternPlugin() = PatternAnnotationPlugin()

}

//@Component
//@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
open class NotNullAnnotationPlugin : ModelPropertyBuilderPlugin {

    override fun supports(delimiter: DocumentationType): Boolean = true

    override fun apply(context: ModelPropertyContext) {
        val present = context.annotationOfType<NotNull>() != null || context.metaValidatorFromBeanPresent<NotNull>()
        context.builder.required(present)
    }

}

private inline fun <reified T : Annotation> ModelPropertyContext.annotationOfType(): T? {
    return validatorFromBean(this, T::class.java).or(validatorFromField(this, T::class.java)).orNull()
}

private inline fun <reified T : Annotation> ModelPropertyContext.metaValidatorFromBeanPresent(): Boolean {
    val propertyDefinition = beanPropertyDefinition.orNull() ?: return false
    return propertyDefinition.getter.isMetaAnnotationPresent<T>() || propertyDefinition.field.isMetaAnnotationPresent<T>()
}

private inline fun <reified T : Annotation> AnnotatedMember.isMetaAnnotationPresent(): Boolean {
    return annotated.annotations.find { AnnotationUtils.isAnnotationMetaPresent(it::class.java, T::class.java) } != null
}
