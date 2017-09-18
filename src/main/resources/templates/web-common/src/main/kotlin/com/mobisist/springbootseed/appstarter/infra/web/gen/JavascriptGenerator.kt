package com.mobisist.springbootseed.appstarter.infra.web.gen

import com.mobisist.springbootseed.appstarter.common.jsonStringify
import org.slf4j.LoggerFactory
import java.io.File
import java.io.StringReader
import javax.xml.bind.JAXB

abstract class JavascriptGenerator(val config: JavascriptGeneratorConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Using configuration: ${config.jsonStringify(prettyPrint = true)}")
    }

    private var builder = StringBuilder().apply {
        appendln("/* ***************************************************************** */")
        appendln("/* DO NOT MODIFY THIS FILE AS IT IS GENERATED BY JavascriptGenerator */")
        appendln("/* ***************************************************************** */")
        appendln()
    }

    fun generate() {
        doGenerate()
        with(File(config.destinationFile)) {
            if (!exists()) {
                parentFile.mkdirs()
            }
            writeText(builder.toString())
        }
    }

    abstract fun doGenerate()

    operator fun String.unaryPlus() {
        builder.appendln(this)
    }

}

fun main(args: Array<String>) {
    // TODO refactoring - remove duplicated configuration by reading application.yaml
    val configuration = JAXB.unmarshal(StringReader(System.getProperty("javascriptGenerator")), Configuration::class.java)
    ApiConstantsGenerator(configuration.apiConstants!!).generate()
    OptionsGenerator(configuration.options!!).generate()
}