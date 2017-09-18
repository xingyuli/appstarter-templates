package @packageName@.admin.config

import @packageName@.infra.service.security.IdentityService
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/assets/external/**")
        web.ignoring().antMatchers("/swagger-ui.html")
        web.ignoring().antMatchers("/webjars/springfox-swagger-ui/**")
        web.ignoring().antMatchers("/swagger-resources/**")
        web.ignoring().antMatchers("/v2/**")
    }

    override fun configure(http: HttpSecurity) {
        val noCsrfMethods = arrayOf("GET", "HEAD", "TRACE", "OPTIONS")
        http
                .csrf()
                    .requireCsrfProtectionMatcher {
                        !noCsrfMethods.contains(it.method) && it.getHeader("X-APP-ENV") != "local"
                    }
                    .and()
                .authorizeRequests()
                    .antMatchers("/public/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticate")
                    .defaultSuccessUrl("/")
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
    }

    @Bean
    override fun userDetailsService() = IdentityService.AdminIdentityService()

}
