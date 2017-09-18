package com.mobisist.springbootseed.appstarter.infra.web.jwt

import com.mobisist.springbootseed.appstarter.infra.service.AppStarterProperties
import com.mobisist.springbootseed.appstarter.infra.service.security.UserDetails
import com.mobisist.springbootseed.appstarter.infra.service.security.UserDetailsService
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

open class JwtHelper : InitializingBean {

    private val CLAIMS_KEY_USER_ID = "userId"
    // private val CLAIMS_KEY_PLATFORM = "platform"

    private lateinit var jwtSecret: String

    private val logger = LoggerFactory.getLogger(JwtHelper::class.java)

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    fun setJwtSecret(appStarterProperties: AppStarterProperties) {
        jwtSecret = appStarterProperties.jwtSecret!!
    }

    override fun afterPropertiesSet() {
        logger.info("Using UserDetailsService implementation: ${userDetailsService.javaClass}")
        logger.info("Using secret: ${jwtSecret.replaceRange(4, jwtSecret.length, "********")}")
    }

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role
     * prefilled (extracted from token). If unsuccessful (token is invalid or not containing all required user
     * properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the PortalUser object extracted from specified token or null if a token is invalid.
     */
    fun parseToken(token: String): UserDetails? {
        try {
            val body = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body
            val userId = (body[CLAIMS_KEY_USER_ID] as String).toLong()
            return userDetailsService.loadUserById(userId)
        } catch (e: JwtException) {
            return null
        } catch (e: ClassCastException) {
            return null
        }
    }

    /**
     * Generates a JWT token containing username as subject, and userId and platform as additional claims. These properties
     * are taken from the specified User object. Tokens validity is infinite.
     *
     * @param u the user for which the token will be generated
     * @return the JWT token
     */
    fun generateToken(u: UserDetails): Token {
        val claims = Jwts.claims().setSubject(u.username).setIssuedAt(Date())
        claims.put(CLAIMS_KEY_USER_ID, "${u.typesafeUserId.value}")
        // claims.put(CLAIMS_KEY_PLATFORM, "${u.currentPlatform.bit.byteValue()}")

        val token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, jwtSecret).compact()
        return Token(token, u.typesafeUserId.value)
    }

    data class Token(val token: String, val userId: Long)

}
