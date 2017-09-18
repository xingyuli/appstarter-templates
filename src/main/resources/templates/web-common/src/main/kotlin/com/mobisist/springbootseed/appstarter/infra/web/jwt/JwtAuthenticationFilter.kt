package com.mobisist.springbootseed.appstarter.infra.web.jwt

import com.mobisist.springbootseed.appstarter.infra.web.getAuthToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(requiresAuthenticationRequestMatcher: RequestMatcher) : AbstractAuthenticationProcessingFilter(requiresAuthenticationRequestMatcher) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val authToken = request.getAuthToken() ?: throw JwtTokenMissingException("No JWT token found in request headers nor cookie")
        return attemptAuthentication(authToken)
    }

    fun attemptAuthentication(token: String): Authentication {
        return authenticationManager.authenticate(JwtAuthenticationToken(token))
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        super.successfulAuthentication(request, response, chain, authResult)

        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain!!.doFilter(request, response)
    }

    class JwtTokenMissingException(message: String) : AuthenticationException(message)

    class JwtAuthenticationToken(val token: String, principal: Any? = null, credentials: Any? = null, authorities: Collection<GrantedAuthority>? = null)
        : UsernamePasswordAuthenticationToken(principal, credentials, authorities)

}