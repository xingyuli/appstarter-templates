package @packageName@.portal.controller

import @packageName@.infra.service.messaging.YunPianTemplate
import com.mobisist.components.messaging.sms.yunpian.v1.YunPianSender
import com.mobisist.springbootseed.appstarter.infra.web.ApiResult
import com.mobisist.springbootseed.appstarter.infra.service.BuiltInErrorCode
import com.mobisist.springbootseed.appstarter.infra.service.RandomHelper
import com.mobisist.springbootseed.appstarter.infra.service.messaging.SmsCodeVerificationInput
import com.mobisist.springbootseed.appstarter.infra.service.messaging.SmsUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author Elias 2017/05/08 11:37
 */
@Api(tags = arrayOf("User"), description = "用户API")
@RestController
@RequestMapping("/public")
open class PublicController {

    private val logger = LoggerFactory.getLogger(PublicController::class.java)

    @Autowired
    private lateinit var yunPianSender: YunPianSender

    @Autowired
    private lateinit var randomHelper: RandomHelper

    @ApiOperation("通过手机号获取验证码")
    @GetMapping("/smsCode", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    open fun smsCode(@RequestParam mobile: String): ResponseEntity<ApiResult<String>> {
        //TODO restrict 60 second
        val code = randomHelper.next(4)
        logger.info("verification code: $code")

        yunPianSender.send(YunPianTemplate.VerificationCodeTemplate(mobile, code, 30))
        return ApiResult.of(SmsUtil.encode(mobile, code))
    }

//    @ApiOperation("通过手机号注册")
//    @PostMapping("/register", consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
//    open fun register(@Valid @RequestBody input: RegisterViaMobileInput): ResponseEntity<ApiResult<*>> {
//        if (!SmsUtil.isCodeValid(input)) {
//            return ApiResult.ofCode(ApiCode.SMS_VERIFICATION_ERROR)
//        }
//
//        quizUserService.openRegistration(input.mobile!!, input.password!!)
//        return ApiResult.ok()
//    }

    @ApiOperation("通过手机号重置密码")
    @PutMapping("/resetPwd", consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    open fun resetPassword(@Valid @RequestBody input: ResetViaMobileInput): ResponseEntity<ApiResult<*>> {
        if (!SmsUtil.isCodeValid(input)) {
            return ApiResult.ofCode(BuiltInErrorCode.SmsErrorCode.VERIFICATION_ERROR)
        }

        TODO("not implemented")
    }

    class RegisterViaMobileInput : SmsCodeVerificationInput() {
        @NotNull
        var password: String? = null
    }

    class ResetViaMobileInput : SmsCodeVerificationInput() {
        @NotNull
        var password: String? = null
    }

}