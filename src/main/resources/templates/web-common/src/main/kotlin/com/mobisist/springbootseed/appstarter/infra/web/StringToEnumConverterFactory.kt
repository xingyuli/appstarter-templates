package com.mobisist.springbootseed.appstarter.infra.web

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

class StringToEnumConverterFactory : ConverterFactory<String, Enum<*>> {

    private val converters = mutableMapOf<Class<*>, Converter<String, *>>()

    override fun <T : Enum<*>> getConverter(targetType: Class<T>): Converter<String, T> {
        val converter = converters.getOrPut(targetType) { StringToEnum(targetType) }

        @Suppress("UNCHECKED_CAST")
        return converter as Converter<String, T>
    }

    private class StringToEnum<T : Enum<*>>(val enumType: Class<T>) : Converter<String, T> {

        override fun convert(source: String): T {
            return enumType.enumConstants.find { it.name == source.toUpperCase() }
                    ?: throw IllegalArgumentException("No enum constant ${enumType.name}.${source.toUpperCase()}")
        }

    }

}