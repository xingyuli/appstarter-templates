package com.mobisist.springbootseed.appstarter.infra.web.jwt

import com.mobisist.springbootseed.appstarter.infra.web.getAuthToken
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

class TokenPresentRequestMatcher : RequestMatcher {
    override fun matches(request: HttpServletRequest) = request.getAuthToken() != null
}