package com.mobisist.springbootseed.appstarter.infra.web.gen

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "configuration")
data class Configuration(
        @get:XmlElement(name = "apiConstantsGenerator", required = true)
        var apiConstants: ApiConstantsGeneratorConfig? = null,

        @get:XmlElement(name = "optionsGenerator", required = true)
        var options: OptionsGeneratorConfig? = null
)

interface JavascriptGeneratorConfig {
    var destinationFile: String?
}

data class ApiConstantsGeneratorConfig(
        @get:XmlElement(required = true) override var destinationFile: String? = null,
        @get:XmlElement(required = true) var controllerPackage: String? = null,
        @get:XmlElement(required = true) var customErrorCodeClass: String? = null) : JavascriptGeneratorConfig

data class OptionsGeneratorConfig(
        @get:XmlElement(required = true) override var destinationFile: String? = null,
        @get:XmlElement(name = "enumPackage") var enumPackages: List<String>? = null) : JavascriptGeneratorConfig
