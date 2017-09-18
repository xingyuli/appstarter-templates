package @packageName@.infra.service.messaging

import com.mobisist.components.messaging.sms.SmsMessage

sealed class YunPianTemplate(mobile: String, text: String) : SmsMessage(mobile, text) {

    class VerificationCodeTemplate(mobile: String, code: String, expireAfterMinutes: Long) : YunPianTemplate(
            mobile, "【AppSignature】您的验证码是${code}, 有效期为${expireAfterMinutes}分钟。")

    class InitialPasswordTemplate(mobile: String, password: String) : YunPianTemplate(
            mobile, "【AppSignature】您的初始密码为${password}，请勿泄露该登录密码。")

    class ResetPasswordTemplate(mobile: String, password: String) : YunPianTemplate(
            mobile, "【AppSignature】您的新密码是${password}")

}