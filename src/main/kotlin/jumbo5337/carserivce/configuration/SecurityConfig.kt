package jumbo5337.carserivce.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/v1/app-info").permitAll()
            .antMatchers("/api/v1/session/**").hasAnyAuthority("CUSTOMER")
            .antMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN")
            .and()
            .httpBasic()
        return http.build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}