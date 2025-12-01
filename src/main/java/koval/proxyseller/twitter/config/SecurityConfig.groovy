package koval.proxyseller.twitter.config

import jakarta.servlet.http.HttpServletResponse
import koval.proxyseller.twitter.security.JwtAuthenticationFilter
import koval.proxyseller.twitter.security.filter.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
class SecurityConfig {

    private final UserDetailsService userDetailsService
    private final JwtAuthenticationFilter jwtAuthenticationFilter
    private final RequestIdFilter requestIdFilter
    private final RequestLoggingFilter requestLoggingFilter
    private final SecurityAuditFilter securityAuditFilter
    private final PerformanceMonitoringFilter performanceMonitoringFilter
    private final RateLimitingFilter rateLimitingFilter

    SecurityConfig(
            UserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RequestIdFilter requestIdFilter,
            RequestLoggingFilter requestLoggingFilter,
            SecurityAuditFilter securityAuditFilter,
            PerformanceMonitoringFilter performanceMonitoringFilter,
            RateLimitingFilter rateLimitingFilter) {
        this.userDetailsService = userDetailsService
        this.jwtAuthenticationFilter = jwtAuthenticationFilter
        this.requestIdFilter = requestIdFilter
        this.requestLoggingFilter = requestLoggingFilter
        this.securityAuditFilter = securityAuditFilter
        this.performanceMonitoringFilter = performanceMonitoringFilter
        this.rateLimitingFilter = rateLimitingFilter
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(12)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/error",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityAuditFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(performanceMonitoringFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                            response.setContentType("application/json")
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}")
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN)
                            response.setContentType("application/json")
                            response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}")
                        })
                )
                .build()
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration()
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:8080"
        ))
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"))
        configuration.setAllowedHeaders(Arrays.asList("*"))
        configuration.setExposedHeaders(Arrays.asList(
                "X-Request-ID",
                "X-Response-Time",
                "X-Memory-Used",
                "X-RateLimit-Limit-Minute",
                "X-RateLimit-Remaining-Minute",
                "X-RateLimit-Limit-Hour",
                "X-RateLimit-Remaining-Hour"
        ))
        configuration.setAllowCredentials(true)
        configuration.setMaxAge(3600L)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager()
    }
}
