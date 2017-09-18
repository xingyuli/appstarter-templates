package com.mobisist.springbootseed.appstarter.infra.service

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class AppCtxHolder : ApplicationContextAware {

    override fun setApplicationContext(applicationContext: ApplicationContext?) {
        context = applicationContext!!
    }

    companion object {

        @JvmStatic
        lateinit var context: ApplicationContext

        inline fun <reified T : Any> beanOf(): T = context.getBean(T::class.java)

    }

}