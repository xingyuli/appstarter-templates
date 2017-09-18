package com.mobisist.springbootseed.appstarter.infra.web.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationSuccessHandler : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        // val jwtToken = authentication as JwtAuthenticationFilter.JwtAuthenticationToken
        // We do not need to do anything extra on REST authentication success, because there is no page to redirect to
    }

}