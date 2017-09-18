package com.mobisist.springbootseed.appstarter.infra.service

import com.mobisist.springbootseed.appstarter.common.jsonStringify
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 系统配置
 * @author Elias 2017/03/02 17:08
 */
@ConfigurationProperties(prefix = "appstarter")
@Component
open class AppStarterProperties : InitializingBean {

    private val logger = LoggerFactory.getLogger(AppStarterProperties::class.java)

    private val sensitiveKeys = arrayOf("randomSeed", "jwtSecret")

    // default values
    private val config = mutableMapOf<String, Any?>(
    )

    // seed for RandomHelper
    var randomSeed: String? by config

    // secret for generating java web token
    var jwtSecret: String? by config

    // 资源服务: 文件存储路径
    var upload: String? by config

    // 资源服务: 资源服务器域名
    var resourceDomainName: String? by config

    var portalDomainName: String? by config

    var appName: String? by config

    // 微信支付成功的回调地址
    var wechatPayNotifyUrl: String? by config

    // 用于注册自定义的jackson枚举反序列化器
    @Suppress("unchecked_cast")
    var enumPackages: List<String>?
        get() = config["enumPackages"] as? List<String>
        set(value) {
            config["enumPackages"] = value
        }

    override fun afterPropertiesSet() {
        logger.info("application configurations are: ${config.filter { !sensitiveKeys.contains(it.key) }.jsonStringify(prettyPrint = true)}")
    }

}