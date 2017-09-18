package @packageName@.portal.controller

import @packageName@.infra.service.security.IdentityService
import @packageName@.infra.service.security.IdmUser
import com.mobisist.springbootseed.appstarter.infra.service.security.UserId
import com.mobisist.springbootseed.appstarter.infra.web.ApiResult
import com.mobisist.springbootseed.appstarter.infra.web.getAuthToken
import com.mobisist.springbootseed.appstarter.infra.web.jwt.JwtAuthenticationFilter
import com.mobisist.springbootseed.appstarter.infra.web.jwt.JwtHelper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

data class LoginSuccessResource(val token: String)

@Api(tags = arrayOf("Login"), description = "登录API")
@RestController
open class LoginController {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @Autowired
    private lateinit var identityService: IdentityService

    @Autowired
    private lateinit var authenticationFilter: JwtAuthenticationFilter

    @Autowired
    private lateinit var jwtHelper: JwtHelper

    @ApiOperation("登录", notes = "用手机号或用户名登录")
    @PostMapping("/authenticate", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    open fun authenticate(@ApiParam("用户名或手机号") @RequestParam(required = false) username: String?,
                          @ApiParam("密码") @RequestParam(required = false) password: String?,
                          @ApiParam("登录平台") @RequestParam(required = false) loginPlatform: String? /* android, ios */,
                          req: HttpServletRequest): ResponseEntity<ApiResult<LoginSuccessResource>> {

        // NOTE: explicit login take precedence over token login
        if (username != null && password != null && loginPlatform != null) {
            return usernameLogin(username, password, loginPlatform)
        }

        val authToken = req.getAuthToken() ?: return ApiResult.badRequest()
        return tokenLogin(authToken)
    }

    private fun usernameLogin(username: String, password: String, loginPlatform: String): ResponseEntity<ApiResult<LoginSuccessResource>> {
        try {
            val idmUser = identityService.loadUserByUsername(username)

            // TODO step 1. return ApiResult.notFound() i.e., http 404 if password not matches

            // TODO step 2. return ApiResult.locked() i.e., http 423 if account is disabled

            val newToken = jwtHelper.generateToken(idmUser).token

            // TODO step 3. update the current loginPlatform and new token to the user, and save the changes in database

            return loginSuccess(idmUser.typesafeUserId, newToken)

        } catch (e: Exception) {
            logger.info("failed to login", e)
            return ApiResult.notFound()
        }
    }

    private fun tokenLogin(token: String): ResponseEntity<ApiResult<LoginSuccessResource>> {
        val authentication = authenticationFilter.attemptAuthentication(token)

        // token will be returned as it is, if no exception was thrown
        val idmUser = authentication.principal as IdmUser
        return loginSuccess(idmUser.typesafeUserId, token)
    }

    private fun loginSuccess(userId: UserId, token: String): ResponseEntity<ApiResult<LoginSuccessResource>> {
        // TODO optional - do something when login success, e.g., record login success behavior, record last login at
        return ApiResult.of(LoginSuccessResource(token))
    }

}