package com.mobisist.springbootseed.appstarter.infra.web.gen

import org.swordess.common.lang.Classes

class OptionsGenerator(config: OptionsGeneratorConfig) : JavascriptGenerator(config) {

    override fun doGenerate() {
        +"var OPTIONS = window.OPTIONS || {};"

        (config as OptionsGeneratorConfig).enumPackages?.forEach { enumPackage ->
            Classes.underPackage(enumPackage) { it.java.isEnum }.forEach { enumType ->
                +""
                +"OPTIONS.${enumType.simpleName} = {};"
                enumType.java.enumConstants.forEach {
                    val enumInstance = it as Enum<*>
                    +"OPTIONS.${enumType.simpleName}.${enumInstance.name} = '${enumInstance.name}';"
                }
            }
        }
    }

}
