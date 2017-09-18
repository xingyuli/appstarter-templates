package com.mobisist.springbootseed.appstarter.common

fun String.camelCaseToUnderscoreSeparated()
        = Regex("[A-Z]{2,}|([A-Z]?[a-z0-9]+)").findAll(this).map { it.value.decapitalize() }.joinToString("_")
