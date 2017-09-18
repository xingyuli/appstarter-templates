package com.mobisist.springbootseed.appstarter.infra.service.messaging

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.mobisist.springbootseed.appstarter.common.DESPlus
import com.mobisist.springbootseed.appstarter.common.jsonFrom
import com.mobisist.springbootseed.appstarter.common.jsonStringify
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class SmsCodeResource @JsonCreator constructor(
        @param:JsonProperty("mobile") val mobile: String,
        @param:JsonProperty("code") val code: String,
        @param:JsonProperty("timestamp") val timestamp: LocalDateTime)

open class SmsCodeVerificationInput {
    @NotNull
    var mobile: String? = null
    @NotNull
    var code: String? = null
    @NotNull
    var token: String? = null
}

class SmsUtil {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(SmsUtil::class.java)

        @JvmStatic
        fun encode(mobile: String, code: String): String {
            val resource = SmsCodeResource(mobile, code, LocalDateTime.now())
            return DESPlus().encrypt(resource.jsonStringify())
        }

        @JvmStatic
        fun isCodeValid(input: SmsCodeVerificationInput): Boolean {
            try {
                val resource = SmsCodeResource::class.jsonFrom(DESPlus().decrypt(input.token!!))
                return input.code == resource.code && input.mobile == resource.mobile && !resource.isExpired()
            } catch (e: Exception) {
                logger.info("token is not valid ${input.token}", e)
                return false
            }
        }

    }

}

private fun SmsCodeResource.isExpired(against: Duration = Duration.ofMinutes(30)): Boolean = timestamp.plus(against).isBefore(LocalDateTime.now())