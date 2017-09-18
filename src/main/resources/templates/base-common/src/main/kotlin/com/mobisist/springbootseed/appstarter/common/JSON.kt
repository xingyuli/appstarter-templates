package com.mobisist.springbootseed.appstarter.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import kotlin.reflect.KClass

private val objectMapper = ObjectMapper().apply {
    findAndRegisterModules()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
}

private val objectMapperPrettyPrint = ObjectMapper().apply {
    findAndRegisterModules()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    enable(SerializationFeature.INDENT_OUTPUT)
}

fun <T : Any> KClass<T>.jsonFrom(json: String): T = objectMapper.readValue(json, this.java)

fun <T> T.jsonStringify(prettyPrint: Boolean = false): String
        = if (prettyPrint) objectMapperPrettyPrint.writeValueAsString(this) else objectMapper.writeValueAsString(this)
