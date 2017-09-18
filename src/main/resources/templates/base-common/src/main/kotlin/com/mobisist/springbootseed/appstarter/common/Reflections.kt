package com.mobisist.springbootseed.appstarter.common

import java.lang.reflect.Method

fun <T : Annotation> Method.mustAnnotation(annotationType : Class<T>): T {
    if (!isAnnotationPresent(annotationType)) {
        throw IllegalArgumentException("annotation ${annotationType.name} is not found")
    }
    return getAnnotation(annotationType)
}
