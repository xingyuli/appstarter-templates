package com.mobisist.springbootseed.appstarter.infra.web.jwt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationProvider : AbstractUserDetailsAuthenticationProvider() {

    @Autowired
    private lateinit var jwtHelper: JwtHelper

    override fun supports(authentication: Class<*>?): Boolean = JwtAuthenticationFilter.JwtAuthenticationToken::class.java.isAssignableFrom(authentication)

    override fun retrieveUser(username: String?, authentication: UsernamePasswordAuthenticationToken?): UserDetails {
        val jwtAuthenticationToken = authentication as JwtAuthenticationFilter.JwtAuthenticationToken
        val token = jwtAuthenticationToken.token

        val userDetails = jwtHelper.parseToken(token) ?: throw JwtTokenMalformedException("JWT token is not valid")

        val previousToken = userDetails.token
        if (!previousToken.isNullOrBlank() && token != previousToken) {
            throw JwtTokenExpiredException(token)
        }

        return userDetails
    }

    override fun additionalAuthenticationChecks(userDetails: UserDetails?, authentication: UsernamePasswordAuthenticationToken?) {
    }

    override fun createSuccessAuthentication(principal: Any?, authentication: Authentication?, user: UserDetails?): Authentication {
        // we need to pass the token back
        val jwtAuth = authentication as JwtAuthenticationFilter.JwtAuthenticationToken
        val defaultResult = super.createSuccessAuthentication(principal, authentication, user)
        return JwtAuthenticationFilter.JwtAuthenticationToken(jwtAuth.token, defaultResult.principal, defaultResult.credentials, defaultResult.authorities)
    }

    class JwtTokenMalformedException(message: String) : AuthenticationException(message)

    class JwtTokenExpiredException(message: String) : AuthenticationException(message)

}