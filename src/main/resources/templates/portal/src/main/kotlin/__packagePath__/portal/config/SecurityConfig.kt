package @packageName@.portal.config

import @packageName@.infra.service.security.IdentityService
import com.mobisist.springbootseed.appstarter.infra.web.jwt.*
import com.mobisist.springbootseed.appstarter.infra.web.and
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.*
import javax.servlet.Filter

@EnableWebSecurity
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    private val writeHttpMethods = arrayOf("PUT", "POST", "DELETE")

    override fun configure(http: HttpSecurity) {
        http
                .csrf()
                    .disable()
                .addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter::class.java)
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    open fun jwtHelper(): JwtHelper = JwtHelper()

    @Bean
    open fun jwtAuthenticationFilter(): Filter {
        val matchers = mutableListOf<RequestMatcher>()
        matchers.add(RequestMatcher { writeHttpMethods.contains(it.method) })

        // TODO different strategy based on environment
        // /docs/**
        //   local - no security required
        //   dev   - security required
        //   prod  - swagger disabled

        val getResourcesThatRequireSignIn = listOf<RestResource>()
        getResourcesThatRequireSignIn.forEach {
            val matcher = it.matcherType.builder().invoke(it.pattern, "GET")

            if (it.optionalToken) {
                matchers.add(matcher.and(TokenPresentRequestMatcher()))
            } else {
                matchers.add(matcher)
            }
        }

        val requestMatcher = AndRequestMatcher(
                NegatedRequestMatcher(OrRequestMatcher(
                        AntPathRequestMatcher("/authenticate", "POST"),
                        AntPathRequestMatcher("/wxpay/payedNotify"),
                        AntPathRequestMatcher("/public/**")
                )),
                OrRequestMatcher(*matchers.toTypedArray())
        )

        return JwtAuthenticationFilter(requestMatcher).apply {
            setAuthenticationManager(authenticationManager())
            setAuthenticationSuccessHandler(JwtAuthenticationSuccessHandler())
            setAuthenticationFailureHandler(JwtAuthenticationFailureHandler())
        }
    }

    override fun authenticationManager(): AuthenticationManager {
        return ProviderManager(listOf(authenticationProvider()))
    }

    @Bean
    override fun userDetailsService() = IdentityService.PortalIdentityService()

    @Bean
    open fun authenticationProvider(): AuthenticationProvider = JwtAuthenticationProvider()

    private class RestResource(val pattern: String, val optionalToken: Boolean = false, val matcherType: MatcherType = MatcherType.ANT)

    private enum class MatcherType {

        ANT {
            override fun builder(): (String, String) -> RequestMatcher = ::AntPathRequestMatcher
        },

        REGEX {
            override fun builder(): (String, String) -> RequestMatcher = ::RegexRequestMatcher
        };

        abstract fun builder(): (String, String) -> RequestMatcher

    }

}
