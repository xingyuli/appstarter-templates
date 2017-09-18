package com.mobisist.springbootseed.appstarter.infra.service.messaging

import com.mobisist.components.messaging.baidupush.BaiduPushSender
import com.mobisist.components.messaging.baidupush.DeviceType
import com.mobisist.components.messaging.baidupush.MessageType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

class DeviceTypeSpecificChannelId(val value: String, val deviceType: DeviceType)

fun String.channelIdAt(deviceType: DeviceType) = DeviceTypeSpecificChannelId(this, deviceType)

fun String.channelIdAt(deviceType: String) = channelIdAt(enumValueOf<DeviceType>(deviceType))

@Component
open class BaiduPushHelper {

    private val logger = LoggerFactory.getLogger(BaiduPushHelper::class.java)

    @Autowired
    private lateinit var baiduPushSender: BaiduPushSender

    fun send(channelId: DeviceTypeSpecificChannelId?, title: String, description: String, customContent: Map<String, Any>? = null) {
        if (channelId == null) {
            logger.info("cannot send msg as the channelId is null")
            return
        }

        val msg = when (channelId.deviceType) {
            DeviceType.ANDROID -> composeAndroidMsg(channelId.value, title, description, customContent)
            DeviceType.IOS -> composeIosMsg(channelId.value, title, description, customContent)
        }
        baiduPushSender.send(msg)
    }

    private fun composeAndroidMsg(channelId: String, title: String, description: String, customContent: Map<String, Any>?) = baiduPushSender.buildMsgToAndroid {
        pushMsgToSingleDeviceRequest {
            messageType = MessageType.NOTIFICATION.rawValue
            msgExpires = 3600 * 5 // 5 hours
            this.channelId = channelId

            this@buildMsgToAndroid.title = title
            this@buildMsgToAndroid.description = description
            customContent?.let {
                this@buildMsgToAndroid.custom_content = it
            }
        }
    }

    private fun composeIosMsg(channelId: String, title: String, description: String, customContent: Map<String, Any>?) = baiduPushSender.buildMsgToIOS {
        pushMsgToSingleDeviceRequest {
            messageType = MessageType.NOTIFICATION.rawValue
            msgExpires = 3600 * 5 // 5 hours
            this.channelId = channelId

            this@buildMsgToIOS.alert = "$title：$description"
            this@buildMsgToIOS.sound = "default"
            customContent?.let {
                for ((k, v) in it) {
                    k setTo v
                }
            }
        }
    }

}