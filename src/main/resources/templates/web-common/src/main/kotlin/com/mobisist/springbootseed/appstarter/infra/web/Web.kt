package com.mobisist.springbootseed.appstarter.infra.web

import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.swordess.common.lang.withPrefix
import org.swordess.common.lang.withoutSuffix
import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.absoluteUrlOf(uri: String) = "${getAuthority()}${uri.withPrefix("/")}"

fun HttpServletRequest.getAuthority(): String = requestURL.substring(0, requestURL.length - requestURI.length).withoutSuffix("/")

fun HttpServletRequest.getAuthToken(cookie: String = "XAUTHTOKEN"): String? {
    var authToken: String? = cookies?.filter { it.name == cookie }?.firstOrNull()?.value

    if (authToken == null) {
        // continue to find the authToken in header
        val header = getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            authToken = header.substring(7)
        }
    }

    return authToken
}

fun RequestMatcher.and(matcher: RequestMatcher) = AndRequestMatcher(this, matcher)

fun RequestMatcher.or(matcher: RequestMatcher) = OrRequestMatcher(this, matcher)

fun RequestMatcher.not() = NegatedRequestMatcher(this)
