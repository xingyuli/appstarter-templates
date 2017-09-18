package com.mobisist.springbootseed.appstarter.infra.web.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.EnumResolver
import com.mobisist.springbootseed.appstarter.infra.service.AppCtxHolder
import com.mobisist.springbootseed.appstarter.infra.service.AppStarterProperties
import com.mobisist.springbootseed.appstarter.infra.web.EnumCaseInsensitiveDeserializer
import com.mobisist.springbootseed.appstarter.infra.web.PageableHandlerMethodArgumentResolver
import com.mobisist.springbootseed.appstarter.infra.web.StringToEnumConverterFactory
import com.mobisist.springbootseed.appstarter.infra.web.plugin.JQueryDataTablePropertyFilterHandlerMethodArgumentResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.format.FormatterRegistry
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.swordess.common.lang.Classes

open class CustomWebMvcConfigurerAdapter : WebMvcConfigurerAdapter() {

    @Autowired
    private lateinit var appStarterProperties: AppStarterProperties

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(PageableHandlerMethodArgumentResolver())
        argumentResolvers.add(JQueryDataTablePropertyFilterHandlerMethodArgumentResolver())
    }

    override fun addFormatters(registry: FormatterRegistry) {
        // replace the default case sensitive enum converter with case-insensitive implementation
        registry.removeConvertible(String::class.java, Enum::class.java)
        registry.addConverterFactory(StringToEnumConverterFactory())
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // Q: Object Mapper Customizations for Swagger2 Not Working?
        // A: Sometimes there are multiple ObjectMapper in play and it may result in the customizations not working.
        // Spring Boot in HttpMessageConverters first adds the Spring Boot configured
        // MappingJackson2HttpMessageConverter and then it adds the default MappingJackson2HttpMessageConverter from
        // Spring MVC. This causes the ObjectMapperConfigured event to fire twice, first for the configured converter
        // (which is actually used) and then for the default converter. So when you f.e. set a custom property naming
        // strategy then in ObjectMapperBeanPropertyNamingStrategy this is overwritten by the second event.
        var objectMapper: ObjectMapper? = null
        converters.filterIsInstance<MappingJackson2HttpMessageConverter>().forEach {
            if (objectMapper == null) {
                objectMapper = it.objectMapper
            } else {
                it.objectMapper = objectMapper
            }
        }
    }

    @Bean
    open fun enumCaseInsensitiveDeserializer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            appStarterProperties.enumPackages?.forEach { enumPackage ->
                Classes.underPackage(enumPackage) {
                    it.java.isEnum
                }.forEach {
                    @Suppress("UNCHECKED_CAST")
                    val enumType = it.java as Class<Enum<*>>
                    builder.deserializerByType(enumType, EnumCaseInsensitiveDeserializer(EnumResolver.constructUsingToString(enumType, null)))
                }
            }
        }
    }

    @Bean
    open fun applicationContextHolder() = AppCtxHolder()

}