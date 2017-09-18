package com.mobisist.springbootseed.appstarter.infra.service

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
open class RandomHelper {

    enum class RandomStrategy {
        NUMERIC,
        ALPHABETIC,
        ALPHANUMERIC
    }

    private lateinit var seed: String

    private val random: Random by lazy {
        SecureRandom(seed.toByteArray())
    }

    @Autowired
    fun setSeed(appStarterProperties: AppStarterProperties) {
        seed = appStarterProperties.randomSeed!!
    }

    @JvmOverloads
    fun next(length: Int, strategy: RandomStrategy = RandomStrategy.NUMERIC): String = when(strategy) {
        RandomStrategy.NUMERIC -> random(length, includeAlphabetic = false, includeNumeric = true)
        RandomStrategy.ALPHABETIC -> random(length, includeAlphabetic = true, includeNumeric = false)
        RandomStrategy.ALPHANUMERIC -> random(length, includeAlphabetic = true, includeNumeric = true)
    }

    fun nextTradeNo(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val no = next(6)
        return "$timestamp$no"
    }

    private fun random(length: Int, includeAlphabetic: Boolean, includeNumeric: Boolean): String {
        return RandomStringUtils.random(length, 0, 0, includeAlphabetic, includeNumeric, null, random)
    }

}
